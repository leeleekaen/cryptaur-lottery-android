package com.cryptaur.lottery.buytickets;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.cryptaur.lottery.Const;
import com.cryptaur.lottery.InteractionListener;
import com.cryptaur.lottery.R;
import com.cryptaur.lottery.model.GetObjectCallback;
import com.cryptaur.lottery.model.Keeper;
import com.cryptaur.lottery.transport.SessionTransport;
import com.cryptaur.lottery.transport.Transport;
import com.cryptaur.lottery.transport.base.NetworkRequest;
import com.cryptaur.lottery.transport.exception.ServerException;
import com.cryptaur.lottery.transport.model.CurrentDraws;
import com.cryptaur.lottery.transport.model.Draw;
import com.cryptaur.lottery.transport.model.Lottery;
import com.cryptaur.lottery.transport.model.Money;
import com.cryptaur.lottery.transport.model.Ticket;
import com.cryptaur.lottery.transport.model.Transaction;
import com.cryptaur.lottery.util.Strings;

import java.math.BigInteger;
import java.util.List;
import java.util.Locale;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link InteractionListener}
 * interface.
 */
public class BuyTicketFragment extends Fragment implements BuyTicketRecyclerViewAdapter.NumbersListener, GetObjectCallback, View.OnClickListener, NetworkRequest.NetworkRequestListener<Transaction> {

    private static final String ARG_LOTTERY = "lottery";
    private ViewGroup root;
    private RecyclerView recyclerView;
    private Button buyButton;
    private InteractionListener mListener;
    private Lottery lottery;
    private Draw currentDraw;
    private Button clearButton;
    private TextView selectNumbersLabel;
    private TextView bottomMessageText;
    private BuyTicketRecyclerViewAdapter adapter;

    private boolean noAddress = false;

    public BuyTicketFragment() {
    }

    public static BuyTicketFragment newInstance(Lottery lottery) {
        BuyTicketFragment fragment = new BuyTicketFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_LOTTERY, lottery);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            lottery = (Lottery) getArguments().getSerializable(ARG_LOTTERY);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = (ViewGroup) inflater.inflate(R.layout.fragment_buy_ticket, container, false);
        Context context = root.getContext();

        Keeper.getInstance(root.getContext()).getCurrentDraws(this, false);

        recyclerView = root.findViewById(R.id.list);
        buyButton = root.findViewById(R.id.buyButton);
        selectNumbersLabel = root.findViewById(R.id.selectNumbers);
        clearButton = root.findViewById(R.id.buttonClear);
        bottomMessageText = root.findViewById(R.id.bottomMessageText);

        recyclerView.setLayoutManager(new GridLayoutManager(context, 6));
        adapter = new BuyTicketRecyclerViewAdapter(lottery, this);
        if (savedInstanceState != null) {
            adapter.loadFromBundle(savedInstanceState);
        }
        recyclerView.setAdapter(adapter);

        clearButton.setOnClickListener(this);
        buyButton.setOnClickListener(this::onBuyTicketButtonPressed);
        bottomMessageText.setOnClickListener(this);

        buyButton.setVisibility(View.VISIBLE);
        bottomMessageText.setVisibility(View.GONE);
        noAddress = SessionTransport.INSTANCE.getAddress() == null;

        fillControls();
        return root;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (adapter != null) {
            adapter.saveToBundle(outState);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof InteractionListener) {
            mListener = (InteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void fillControls() {
        if (buyButton != null) {
            if (noAddress) {
                buyButton.setText(R.string.login);
            } else if (currentDraw != null) {
                String text = Strings.toDecimalString(currentDraw.getTicketPrice().amount, 8, 0, ".", ",");
                text = buyButton.getResources().getString(R.string.buy_for__cpt, text);
                buyButton.setText(text);
                checkBalance();
            }
            buyButton.setEnabled(noAddress || adapter.isFilled());
        }
        if (selectNumbersLabel != null) {
            String selectLabel = selectNumbersLabel.getResources().getString(R.string.select_d_numbers, lottery.getNumbersAmount());
            selectNumbersLabel.setText(selectLabel);
        }
    }

    private void checkBalance() {
        Keeper.getInstance(buyButton.getContext()).getBalance(new GetObjectCallback<BigInteger>() {
            @Override
            public void onRequestResult(BigInteger responce) {
                if (responce.compareTo(currentDraw.getTicketPrice().amount) < 0) {
                    buyButton.setVisibility(View.GONE);
                    bottomMessageText.setVisibility(View.VISIBLE);
                } else {
                    buyButton.setVisibility(View.VISIBLE);
                    bottomMessageText.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNetworkRequestError(Exception e) {

            }

            @Override
            public void onCancel() {

            }
        }, false);
    }

    @Override
    public void onNumbersChanged(List<Integer> numbers, boolean filled) {
        buyButton.setEnabled(noAddress || filled);
        if (filled) {
            Keeper.getInstance(root.getContext()).updateTicketFee(currentDraw, null);
        }
    }

    @Override
    public void onRequestResult(Object responce) {
        if (responce instanceof CurrentDraws) {
            CurrentDraws currentDraws = (CurrentDraws) responce;
            currentDraw = currentDraws.getDraw(lottery);
        }
        fillControls();
    }

    @Override
    public void onNetworkRequestError(Exception e) {

    }

    @Override
    public void onCancel() {

    }

    private void doBuyTicket() {
        List<Integer> numbers = adapter.getCheckedNumbers();
        Ticket ticket = Ticket.buyTicket(currentDraw, numbers);
        Transport.INSTANCE.buyTicket(ticket, this);
        Keeper.getInstance(root.getContext()).refreshTickets();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonClear:
                if (adapter != null)
                    adapter.clear();
                break;

            case R.id.bottomMessageText:
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(Const.URL_CRYPTAUR_WALLET));
                startActivity(intent);
                break;
        }
    }

    private void onBuyTicketButtonPressed(View v) {
        if (!SessionTransport.INSTANCE.isLoggedIn()) {
            mListener.doAction(InteractionListener.Action.Login, this);
        } else {
            if (currentDraw.getTicketPrice().age() < 60_000 && currentDraw.getTicketPrice().fee != null) {
                showBuyTicketDialog();
            } else {
                Toast.makeText(v.getContext(), R.string.updatingTicketFee, Toast.LENGTH_SHORT).show();
                Keeper.getInstance(v.getContext()).updateTicketFee(currentDraw, new GetObjectCallback<Money>() {
                    @Override
                    public void onRequestResult(Money responce) {
                        showBuyTicketDialog();
                    }

                    @Override
                    public void onNetworkRequestError(Exception e) {
                        if (e instanceof ServerException) {
                            ServerException se = (ServerException) e;
                            if (se.errorCode == 400) {
                                Resources res = root.getResources();
                                StringBuilder bld = new StringBuilder();
                                BigInteger min = currentDraw.getTicketPrice().amount.multiply(BigInteger.valueOf(12)).divide(BigInteger.valueOf(10));
                                bld.append(res.getString(R.string.buy_ticket_includes_fee))
                                        .append("\n")
                                        .append(res.getString(R.string.you_need)).append(" ")
                                        .append(Strings.toDecimalString(min, 8, 0, ".", "."))
                                        .append(" ").append(getString(R.string.cpt_to_buy));

                                new AlertDialog.Builder(root.getContext())
                                        .setTitle(R.string.error)
                                        .setMessage(bld)
                                        .setPositiveButton(android.R.string.ok, (dialog, which) -> dialog.dismiss())
                                        .show();
                            }
                        } else
                            Toast.makeText(v.getContext(), R.string.errorUpdatingTicketFee, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancel() {
                    }
                });
            }
        }
    }

    private void showBuyTicketDialog() {
        List<Integer> numbers = adapter.getCheckedNumbers();
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append("Buy ticket with numbers ");
        for (int i = 0; i < numbers.size(); i++) {
            strBuilder.append(String.format(Locale.getDefault(), "%d", numbers.get(i)));
            if (i < numbers.size() - 1)
                strBuilder.append(", ");
        }
        BigInteger total = currentDraw.getTicketPrice().amount.add(currentDraw.getTicketPrice().fee);
        strBuilder.append(" for ")
                .append(Strings.toDecimalString(currentDraw.getTicketPrice().amount, 8, 0, ".", ","))
                .append(" CPT?\n")
                .append("Transaction fee: ")
                .append(Strings.toDecimalString(currentDraw.getTicketPrice().fee, 8, 0, ".", ","))
                .append(" CPT.\n")
                .append("Total: ")
                .append(Strings.toDecimalString(total, 8, 0, ".", ","))
                .append(" CPT.\n");

        new AlertDialog.Builder(getActivity())
                .setTitle("Buy ticket?")
                .setMessage(strBuilder)
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    doBuyTicket();
                    dialog.dismiss();
                }).setNegativeButton(android.R.string.no, (dialog, which) -> dialog.dismiss())
                .show();
    }

    @Override
    public void onNetworkRequestStart(NetworkRequest request) {
    }

    @Override
    public void onNetworkRequestDone(NetworkRequest request, Transaction responce) {
        Toast.makeText(root.getContext(), R.string.boughtTicket, Toast.LENGTH_SHORT).show();
        if (mListener != null)
            mListener.doAction(InteractionListener.Action.CloseThisFragment, this);
    }

    @Override
    public void onNetworkRequestError(NetworkRequest request, Exception e) {
        Toast.makeText(root.getContext(), R.string.requestError, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCancel(NetworkRequest request) {
        Toast.makeText(root.getContext(), R.string.requestCancelled, Toast.LENGTH_SHORT).show();
    }
}
