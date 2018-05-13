package com.cryptaur.lottery;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.drawable.DrawerArrowDrawable;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

import com.cryptaur.lottery.controller.InitialLoginController;
import com.cryptaur.lottery.controller.PinLoginController;
import com.cryptaur.lottery.controller.WorkflowController;
import com.cryptaur.lottery.dialog.HowToPlayDialogFragment;
import com.cryptaur.lottery.dialog.MenuDialogFragment;
import com.cryptaur.lottery.model.ActionShowTransactionFailed;
import com.cryptaur.lottery.model.CPT;
import com.cryptaur.lottery.model.GetObjectCallback;
import com.cryptaur.lottery.model.Keeper;
import com.cryptaur.lottery.transport.SessionTransport;
import com.cryptaur.lottery.transport.Transport;
import com.cryptaur.lottery.transport.model.CurrentDraws;
import com.cryptaur.lottery.transport.model.Money;
import com.cryptaur.lottery.transport.model.Ticket;
import com.cryptaur.lottery.transport.model.Transaction;
import com.cryptaur.lottery.transport.model.TransactionBuyTicket;
import com.cryptaur.lottery.transport.model.TransactionGetTheWin;
import com.cryptaur.lottery.view.WalletViewHolder;

public abstract class ActivityBase extends AppCompatActivity implements InteractionListener, GetObjectCallback<CurrentDraws> {

    private final MenuHelper menuHelper = new MenuHelper(this);
    protected boolean isHomeAsUp = false;
    private WalletViewHolder walletView;
    private DrawerArrowDrawable homeDrawable;
    private WorkflowController workflowController;
    private Toolbar toolbar;
    private boolean isBackPressed;

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        TextView walletView = findViewById(R.id.wallet);
        if (walletView != null) {
            this.walletView = new WalletViewHolder(walletView, this);
        }

        homeDrawable = new DrawerArrowDrawable(toolbar.getContext());
        toolbar.setNavigationIcon(homeDrawable);
        toolbar.setNavigationOnClickListener(v -> {
            if (isHomeAsUp) {
                onBackPressed();
            } else {
                MenuDialogFragment.showDialog(getSupportFragmentManager());
            }
        });
    }

    @Override
    public void onBackPressed() {
        isBackPressed = true;
        super.onBackPressed();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        menuHelper.onCreateOptionsMenu(menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menuHelper.onPrepareOptionsMenu(menu);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuItem_uncheckedTickets:
                Intent intent = new Intent(this, MyTicketsActivity.class);
                intent.putExtra(MyTicketsActivity.ARG_PAGE, 1);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void doAction(IAction action, @Nullable Fragment fragment) {
        if (workflowController != null && workflowController.onAction(action, fragment))
            return;

        if (workflowController != null && action == Action.FinishWorkflow) {
            workflowController = null;
            return;
        }

        if (action instanceof InteractionListener.Action) {
            switch ((InteractionListener.Action) action) {
                case CloseThisFragment:
                    finish();
                    break;

                case FinishWorkflow:
                    workflowController = null;
                    break;

                case Restart:
                    if (walletView != null)
                        walletView.refresh(true);
                    recreate();
                    break;

                case Login:
                    if (Transport.INSTANCE.canAuthorizeWithPin()) {
                        workflowController = new PinLoginController(this);
                    } else {
                        workflowController = new InitialLoginController(this);
                    }
                    workflowController.start();
                    break;

                case UseLoginAndPassword:
                    workflowController = new InitialLoginController(this);
                    workflowController.start();
                    break;

                case Logout:
                    SessionTransport.INSTANCE.logout();
                    Keeper.getInstance(this).clear();
                    recreate();
                    break;

                case MyTickets:
                    Intent intent = new Intent(this, MyTicketsActivity.class);
                    startActivity(intent);
                    break;

                case HowToPlay:
                    HowToPlayDialogFragment.showDialog(getSupportFragmentManager());
                    break;

                case InvalidateOptionsMenu:
                    invalidateOptionsMenu();
                    break;
            }
        } else if (action instanceof ActionShowTransactionFailed) {
            showTransactionFailed(((ActionShowTransactionFailed) action).transaction);
        }
    }

    @Override
    public void onRequestResult(CurrentDraws responce) {

    }

    @Override
    public void onNetworkRequestError(Exception e) {
    }

    @Override
    public void onCancel() {
    }

    @Override
    protected void onPause() {
        SessionTransport.INSTANCE.onPauseActivity();
        Keeper.getInstance(this).currentDrawsKeeper.removeListener(this);
        menuHelper.onActivityPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        SessionTransport.INSTANCE.onResumeActivity();
        Keeper.getInstance(this).currentDrawsKeeper.addListener(this);
        menuHelper.onActivityResume();
    }

    public void setHomeAsUp(boolean isHomeAsUp) {
        if (this.isHomeAsUp != isHomeAsUp) {
            homeDrawable.setVerticalMirror(false);
            this.isHomeAsUp = isHomeAsUp;
            ValueAnimator anim = isHomeAsUp ? ValueAnimator.ofFloat(0, 1) : ValueAnimator.ofFloat(1, 0);
            anim.addUpdateListener(valueAnimator -> {
                homeDrawable.setProgress((Float) valueAnimator.getAnimatedValue());
            });
            anim.setInterpolator(new DecelerateInterpolator());
            anim.setDuration(400);
            anim.start();
        } else if (isHomeAsUp) {
            homeDrawable.setVerticalMirror(!isBackPressed);
            ValueAnimator anim = ValueAnimator.ofFloat(1, -1);
            anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                boolean part1 = true;

                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float val = (float) animation.getAnimatedValue();
                    if (val < 0) {
                        val = -val;
                        if (part1) {
                            homeDrawable.setVerticalMirror(isBackPressed);
                            isBackPressed = false;
                            part1 = false;
                        }
                    }
                    homeDrawable.setProgress(val);
                }
            });
            anim.setInterpolator(new DecelerateInterpolator());
            anim.setDuration(600);
            anim.start();
        }
    }

    private void showTransactionFailed(Transaction transaction) {
        StringBuilder message = new StringBuilder();
        if (transaction instanceof TransactionBuyTicket) {
            Ticket ticket = ((TransactionBuyTicket) transaction).ticket;
            message.append(getString(R.string.failedToByTicket)).append(ticket.drawIndex)
                    .append(", ").append(getString(R.string.lottery)).append(" ").append(ticket.lottery.displayName())
                    .append("\n").append(getString(R.string.numbers));
            int[] numbers = ticket.numbers;
            for (int i = 0; i < numbers.length; i++) {
                if (i > 0)
                    message.append(", ");
                message.append(numbers[i]);
            }
        } else if (transaction instanceof TransactionGetTheWin) {
            Money amount = ((TransactionGetTheWin) transaction).winAmount;
            message.append(getString(R.string.failedToGetTheWin))
                    .append(CPT.toDecimalString(amount.amount, 0))
                    .append(" ")
                    .append(getString(R.string.cpt))
                    .append("\n").append(getString(R.string.pleaseTryAgain));
        }

        new AlertDialog.Builder(this)
                .setTitle(R.string.transactionfailed)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> dialog.dismiss())
                .show();
    }
}
