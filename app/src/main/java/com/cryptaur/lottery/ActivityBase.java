package com.cryptaur.lottery;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.cryptaur.lottery.controller.WorkflowController;
import com.cryptaur.lottery.login.InitialLoginController;
import com.cryptaur.lottery.login.MenuDialogFragmentFragment;
import com.cryptaur.lottery.transport.Transport;
import com.cryptaur.lottery.view.WalletViewHolder;

public abstract class ActivityBase extends AppCompatActivity implements InteractionListener {

    private final MenuHelper helper = new MenuHelper(this);
    WorkflowController workflowController;
    private WalletViewHolder walletView;

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        TextView walletView = findViewById(R.id.wallet);
        if (walletView != null) {
            this.walletView = new WalletViewHolder(walletView, this);
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        helper.onCreateOptionsMenu(menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        helper.onPrepareOptionsMenu(menu);
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

            case R.id.menuItem_menu:
                MenuDialogFragmentFragment.showDialog(getSupportFragmentManager());
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

                case RefreshWallet:
                    if (walletView != null)
                        walletView.refresh(true);
                    break;

                case Login:
                    workflowController = new InitialLoginController(this);
                    workflowController.start();
                    break;
            }
        }
    }

    @Override
    protected void onPause() {
        Transport.INSTANCE.onPauseActivity();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Transport.INSTANCE.onResumeActivity();
    }
}
