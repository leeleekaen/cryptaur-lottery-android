package com.cryptaur.lottery.buytickets;


import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cryptaur.lottery.R;
import com.cryptaur.lottery.transport.model.Lottery;

import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LotteryRulesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LotteryRulesFragment extends Fragment {
    private static final String ARG_LOTTERY = "lottery";

    private Lottery lottery;

    public LotteryRulesFragment() {
    }

    public static LotteryRulesFragment newInstance(Lottery lottery) {
        LotteryRulesFragment fragment = new LotteryRulesFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_lottery_rules, container, false);
        Resources res = root.getResources();

        TextView participateBody = root.findViewById(R.id.participateBody);
        TextView pricePool1 = root.findViewById(R.id.prize_pool_1);
        TextView pricePool3 = root.findViewById(R.id.prize_pool_3);
        TextView pricePool4 = root.findViewById(R.id.prize_pool_4);
        TextView pricePool5 = root.findViewById(R.id.prize_pool_5);

        String str = res.getString(R.string.participate_body, lottery.getNumbersAmount(), lottery.getMaxValue());
        participateBody.setText(str);

        str = res.getString(R.string.distribution_of_the_prize_pool_body3, lottery.getNumbersAmount());
        pricePool3.setText(str);

        str = res.getString(R.string.distribution_of_the_prize_pool_body4, lottery.getMaxValue());
        pricePool4.setText(str);

        str = res.getString(R.string.distribution_of_the_prize_pool_body5, lottery.getNumbersAmount(), lottery.getMaxValue());
        pricePool5.setText(str);

        double[] shares = lottery.getWinShares();
        int nonZeroes = 0;
        for (int i = 0, sharesLength = shares.length; i < sharesLength; i++) {
            if (shares[i] > 0) {
                ++nonZeroes;
            }
        }

        str = res.getString(R.string.distribution_of_the_prize_pool_body1, nonZeroes);
        pricePool1.setText(str);

        fillDistributionDetails(root);
        return root;
    }

    private void fillDistributionDetails(ViewGroup root) {
        Resources res = root.getResources();
        TextView[] numbers = new TextView[5];
        TextView[] labels = new TextView[5];
        numbers[0] = root.findViewById(R.id.distributionNumberLine1);
        numbers[1] = root.findViewById(R.id.distributionNumberLine2);
        numbers[2] = root.findViewById(R.id.distributionNumberLine3);
        numbers[3] = root.findViewById(R.id.distributionNumberLine4);
        numbers[4] = root.findViewById(R.id.distributionNumberLine5);
        labels[0] = root.findViewById(R.id.distributionTextLine1);
        labels[1] = root.findViewById(R.id.distributionTextLine2);
        labels[2] = root.findViewById(R.id.distributionTextLine3);
        labels[3] = root.findViewById(R.id.distributionTextLine4);
        labels[4] = root.findViewById(R.id.distributionTextLine5);


        double[] shares = lottery.getWinShares();

        int startZeroLines = 0;
        for (int i = shares.length - 1; i >= 0; i--) {
            double share = shares[i];
            int line = shares.length - i - 1;
            if (share > 0) {
                numbers[line].setText(String.format(Locale.getDefault(), "%d", i));
                if (line > 0) {
                    labels[line].setText(res.getString(R.string.distributionExpl, Math.round(share * 100), i));
                }
            } else {
                startZeroLines = line;
                break;
            }
        }
        for (int i = startZeroLines; i < 5; i++) {
            numbers[i].setVisibility(View.GONE);
            labels[i].setVisibility(View.GONE);
        }

    }

}
