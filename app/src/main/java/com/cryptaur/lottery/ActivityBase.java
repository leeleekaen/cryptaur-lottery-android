package com.cryptaur.lottery;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.cryptaur.lottery.controller.WorkflowController;
import com.cryptaur.lottery.login.InitialLoginController;
import com.cryptaur.lottery.login.MenuDialogFragmentFragment;
import com.cryptaur.lottery.transport.Transport;

public class ActivityBase extends AppCompatActivity implements InteractionListener {

    private final MenuHelper helper = new MenuHelper(this);
    WorkflowController workflowController;

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
    public void doAction(IAction action, Fragment fragment) {
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
                    //TODO: implement
                    break;
            }
        }
    }

    protected void doLogin() {
        workflowController = new InitialLoginController(this);
        workflowController.start();
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
