package com.wptdxii.playground.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cloudhome.R;
import com.cloudhome.application.MyApplication;
import com.cloudhome.utils.IpConfig;
import com.cloudhome.view.customview.LinearLayoutForListView;
import com.tencent.mm.sdk.constants.Build;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import net.sourceforge.simcpux.Constants;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

public class RightPayActivity extends BaseActivity {


    private static final String TAG = "MicroMsg.SDKSample.WXPayEntryActivity";

    private String user_id;
    private String token;
    private Map<String, String> key_value = new HashMap<String, String>();
    private Dialog dialog;
    private PriceAdapter padapter;
    private HolderAdapter hdapter;
    private InsuredAdapter idapter;
    private OtherAdapter odapter;
    private BenefitAdapter bdapter;
    private LinearLayoutForListView items_list;
    private LinearLayoutForListView holder_list;
    private LinearLayoutForListView insured_list;
    private LinearLayoutForListView other_list;
    private LinearLayoutForListView benefit_list;
    private TextView period_from, period_to, unit_price,
            the_copies, total_price;
    private ImageView arrow_img, c_p_orders_back;
    private IWXAPI api;
    private RelativeLayout p_i_o_rel, p_i_o_rel2;
    private TextView weixin_pay, need_pay;
    private String order_id, money_pay = "";
    private TextView tv_order_title;
    private TextView tv_order_num;

    @SuppressLint("HandlerLeak")
    private Handler errcode_handler = new Handler() {

        @Override
        public void handleMessage(android.os.Message msg) {
            // Map<String, String> data = (Map<String, String>) msg.obj;
            String data = (String) msg.obj;
            dialog.dismiss();
            String status = data;

            Log.d("455454", "455445" + status);
            if (status.equals("false")) {

                Toast.makeText(RightPayActivity.this,
                        "网络连接失败，请确认网络连接后重试", Toast.LENGTH_SHORT).show();
            }
        }

    };

    private Handler null_handler = new Handler() {

        @Override
        public void handleMessage(android.os.Message msg) {
            Map<String, String> data = (Map<String, String>) msg.obj;

            String errmsg = data.get("errmsg");

            dialog.dismiss();
            Toast.makeText(RightPayActivity.this, errmsg,
                    Toast.LENGTH_SHORT).show();

        }

    };

    public  static RightPayActivity RightPayinstance=null;
    @SuppressLint("HandlerLeak")
    private Handler payhandler = new Handler() {

        @Override
        public void handleMessage(android.os.Message msg) {

            PayReq data = (PayReq) msg.obj;
            Toast.makeText(RightPayActivity.this,
                    "正常调起支付", Toast.LENGTH_SHORT).show();
            // 在支付之前，如果应用没有注册到微信，应该先调用IWXMsg.registerApp将应用注册到微信

            MyApplication.prepay_id = data.prepayId;
            api.sendReq(data);

        }

    };

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @SuppressWarnings("unchecked")
        @Override
        public void handleMessage(android.os.Message msg) {

            Map<String, Object> data = (Map<String, Object>) msg.obj;

            List<Map<String, String>> pricelist = (List<Map<String, String>>) data
                    .get("price_list");
            List<Map<String, String>> holderlist = (List<Map<String, String>>) data
                    .get("holder_list");
            List<Map<String, String>> insuredlist = (List<Map<String, String>>) data
                    .get("insured_list");
            List<Map<String, String>> otherlist = (List<Map<String, String>>) data
                    .get("other_list");
            List<Map<String, String>> benefitlist = (List<Map<String, String>>) data
                    .get("benefit_list");

            dialog.dismiss();

            padapter.setData(pricelist);
            items_list.setAdapter(padapter);
            padapter.notifyDataSetChanged();

            hdapter.setData(holderlist);
            holder_list.setAdapter(hdapter);
            hdapter.notifyDataSetChanged();

            idapter.setData(insuredlist);
            insured_list.setAdapter(idapter);
            idapter.notifyDataSetChanged();

            odapter.setData(otherlist);
            other_list.setAdapter(odapter);
            odapter.notifyDataSetChanged();

            bdapter.setData(benefitlist);
            benefit_list.setAdapter(bdapter);
            bdapter.notifyDataSetChanged();

            // map_all.put("product_id", product_id);


            tv_order_title.setText((String) data.get("product_name"));
            tv_order_num.setText((String) data.get("subs_code"));
            period_from.setText(data.get("period_from") + "至");
            period_to.setText((String) data.get("period_to"));

            unit_price.setText("￥" + data.get("price"));
            the_copies.setText((String) data.get("count"));

            money_pay = (String) data.get("amount");
            total_price.setText("￥" + money_pay);
            need_pay.setText("￥" + money_pay);


            weixin_pay.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View arg0) {


                    boolean isPaySupported = api.getWXAppSupportAPI() >= Build.PAY_SUPPORTED_SDK_INT;

                    if (!isPaySupported) {
                        Toast.makeText(RightPayActivity.this,
                                String.valueOf(isPaySupported),
                                Toast.LENGTH_SHORT).show();
                    } else {
                        String url = IpConfig.getUri("getPrepayOrder");
                        TextView payBtn = (TextView) findViewById(R.id.weixin_pay);
                        payBtn.setEnabled(false);


                        key_value.put("user_id", user_id);
                        key_value.put("token", token);

                        key_value.put("order_id", order_id);

                        key_value.put("amount", money_pay);

                        key_value.put("note", "用户" + order_id + "个险支付");

                        key_value.put("type", "1");


                        setpayData(url);

                        payBtn.setEnabled(true);
                    }

                }
            });

            p_i_o_rel.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View arg0) {

                    // TODO Auto-generated method stub
                    if (p_i_o_rel2.isShown()) {
                        p_i_o_rel2.setVisibility(View.GONE);
                        arrow_img.setImageResource(R.drawable.arrow_down);

                    } else {

                        p_i_o_rel2.setVisibility(View.VISIBLE);
                        arrow_img.setImageResource(R.drawable.arrow_up);

                    }

                }
            });

        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_right_pay);


        user_id = sp.getString("Login_UID", "");
        token = sp.getString("Login_TOKEN", "");
        Intent intent = getIntent();
        order_id = intent.getStringExtra("id");
        MyApplication.prevoius_page  = intent.getStringExtra("prevoius_page");
        MyApplication.java_wxpay_orderno="";

        Log.d("777", order_id);
        api = WXAPIFactory.createWXAPI(this, getString(R.string.weixin_appid));
        api.registerApp(Constants.APP_ID);

        RightPayinstance=this;
        init();
        initEvent();

    }

    void init() {

        key_value.put("order_id", order_id);
        key_value.put("user_id", user_id);
        key_value.put("token", token);


        dialog = new Dialog(this, R.style.progress_dialog);
        dialog.setContentView(R.layout.progress_dialog);
        dialog.setCancelable(true);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        TextView p_dialog = (TextView) dialog.findViewById(R.id.id_tv_loadingmsg);
        p_dialog.setText("请稍后...");


        padapter = new PriceAdapter(RightPayActivity.this);
        hdapter = new HolderAdapter(RightPayActivity.this);
        idapter = new InsuredAdapter(RightPayActivity.this);
        odapter = new OtherAdapter(RightPayActivity.this);
        bdapter = new BenefitAdapter(RightPayActivity.this);

        items_list = (LinearLayoutForListView) findViewById(R.id.items_list);
        holder_list = (LinearLayoutForListView) findViewById(R.id.holder_list);
        insured_list = (LinearLayoutForListView) findViewById(R.id.insured_list);
        other_list = (LinearLayoutForListView) findViewById(R.id.other_list);
        benefit_list = (LinearLayoutForListView) findViewById(R.id.benefit_list);


        arrow_img = (ImageView) findViewById(R.id.arrow_img);
        period_from = (TextView) findViewById(R.id.period_from);

        period_to = (TextView) findViewById(R.id.period_to);

        unit_price = (TextView) findViewById(R.id.unit_price);

        the_copies = (TextView) findViewById(R.id.the_copies);
        total_price = (TextView) findViewById(R.id.total_price);

        weixin_pay = (TextView) findViewById(R.id.weixin_pay);
        need_pay = (TextView) findViewById(R.id.need_pay);

        c_p_orders_back = (ImageView) findViewById(R.id.c_p_orders_back);
        p_i_o_rel = (RelativeLayout) findViewById(R.id.p_i_o_rel);
        p_i_o_rel2 = (RelativeLayout) findViewById(R.id.p_i_o_rel2);


        tv_order_title = (TextView) findViewById(R.id.tv_order_title);
        tv_order_num = (TextView) findViewById(R.id.tv_order_num);
    }

    void initEvent() {

        c_p_orders_back.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                finish();
            }
        });
        dialog.show();
        final String url = IpConfig.getUri("getRecOrderDetail");
        setData(url);

    }

    private void setData(String url) {


        OkHttpUtils.post()//
                .url(url)//
                .params(key_value)//
                .build()//
                .execute(new StringCallback() {

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.e("error", "获取数据异常 ", e);
                        String status = "false";
                        Message message = Message.obtain();

                        message.obj = status;

                        errcode_handler.sendMessage(message);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        String jsonString = response;
                        Log.d("onSuccess", "onSuccess json = " + jsonString);

                        Map<String, Object> map_all = new HashMap<String, Object>();
                        Map<String, String> errcode_map = new HashMap<String, String>();
                        List<Map<String, String>> price_list = new ArrayList<Map<String, String>>();
                        List<Map<String, String>> holder_list = new ArrayList<Map<String, String>>();
                        List<Map<String, String>> insured_list = new ArrayList<Map<String, String>>();
                        List<Map<String, String>> other_list = new ArrayList<Map<String, String>>();
                        List<Map<String, String>> benefit_list = new ArrayList<Map<String, String>>();
                        try {

                            if (jsonString == null || jsonString.equals("")
                                    || jsonString.equals("null")) {
                                String status = "false";
                                Message message = Message.obtain();

                                message.obj = status;

                                errcode_handler.sendMessage(message);
                            } else {

                                JSONObject jsonObject = new JSONObject(jsonString);

                                String errcode = jsonObject.getString("errcode");
                                if (!errcode.equals("0")) {
                                    String errmsg = jsonObject.getString("errmsg");

                                    errcode_map.put("errcode", errcode);
                                    errcode_map.put("errmsg", errmsg);

                                    Message message2 = Message.obtain();

                                    message2.obj = errcode_map;

                                    null_handler.sendMessage(message2);

                                } else {
                                    JSONObject dataObject = jsonObject
                                            .getJSONObject("data");

                                    JSONArray itemsArray = dataObject
                                            .getJSONArray("items");

                                    for (int i = 0; i < itemsArray.length(); i++) {

                                        Map<String, String> map = new HashMap<String, String>();
                                        JSONArray jsonArray1 = itemsArray
                                                .getJSONArray(i);

                                        map.put("policy_name", jsonArray1.getString(0));
                                        map.put("policy_price", jsonArray1.getString(1));

                                        price_list.add(map);
                                    }

                                    JSONArray holderArray = dataObject
                                            .getJSONArray("holder");

                                    for (int i = 0; i < holderArray.length(); i++) {

                                        Map<String, String> map = new HashMap<String, String>();
                                        JSONArray jsonArray1 = holderArray
                                                .getJSONArray(i);

                                        map.put("holder_title", jsonArray1.getString(0));
                                        map.put("holder_value", jsonArray1.getString(1));

                                        holder_list.add(map);
                                    }

                                    JSONArray insuredArray = dataObject
                                            .getJSONArray("insured");

                                    for (int i = 0; i < insuredArray.length(); i++) {

                                        Map<String, String> map = new HashMap<String, String>();
                                        JSONArray jsonArray1 = insuredArray
                                                .getJSONArray(i);

                                        map.put("insured_title",
                                                jsonArray1.getString(0));
                                        map.put("insured_value",
                                                jsonArray1.getString(1));

                                        insured_list.add(map);
                                    }

                                    JSONArray otherArray = dataObject
                                            .getJSONArray("other");

                                    for (int i = 0; i < otherArray.length(); i++) {

                                        Map<String, String> map = new HashMap<String, String>();
                                        JSONArray jsonArray1 = otherArray
                                                .getJSONArray(i);

                                        map.put("other_title", jsonArray1.getString(0));
                                        map.put("other_value", jsonArray1.getString(1));

                                        other_list.add(map);
                                    }

                                    JSONArray benefitArray = dataObject
                                            .getJSONArray("benefit");


                                    Map<String, String> map3 = new HashMap<String, String>();

                                    map3.put("benefit_title", benefitArray.getString(0));
                                    map3.put("benefit_value", benefitArray.getString(1));

                                    benefit_list.add(map3);

                                    map_all.put("price_list", price_list);
                                    map_all.put("holder_list", holder_list);
                                    map_all.put("insured_list", insured_list);
                                    map_all.put("other_list", other_list);
                                    map_all.put("benefit_list", benefit_list);

                                    String subs_code = dataObject
                                            .getString("subs_code");
                                    String product_id = dataObject
                                            .getString("product_id");
                                    String img_url = dataObject.getString("img_url");
                                    String product_name = dataObject
                                            .getString("product_name");
                                    String price = dataObject.getString("price");
                                    String amount = dataObject.getString("amount");
                                    String count = dataObject.getString("count");
                                    String period_from = dataObject
                                            .getString("period_from");
                                    String period_to = dataObject
                                            .getString("period_to");

                                    map_all.put("subs_code", subs_code);
                                    map_all.put("product_id", product_id);
                                    map_all.put("img_url", img_url);
                                    map_all.put("product_name", product_name);

                                    map_all.put("price", price);
                                    map_all.put("amount", amount);
                                    map_all.put("count", count);
                                    map_all.put("period_from", period_from);
                                    map_all.put("period_to", period_to);

                                    Message message = Message.obtain();

                                    message.obj = map_all;
                                    handler.sendMessage(message);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                });


    }

    private void setpayData(String url) {


        OkHttpUtils.post()//
                .url(url)//
                .params(key_value)//
                .build()//
                .execute(new StringCallback() {

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.e("error", "获取数据异常 ", e);
                        String status = "false";
                        Message message = Message.obtain();

                        message.obj = status;

                        errcode_handler.sendMessage(message);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        String jsonString = response;
                        Log.d("onSuccess", "onSuccess json = " + jsonString);
                        Map<String, String> errcode_map = new HashMap<String, String>();
                        try {

                            if (jsonString == null || jsonString.equals("")
                                    || jsonString.equals("null")) {
                                String status = "false";
                                Message message = Message.obtain();

                                message.obj = status;

                                errcode_handler.sendMessage(message);
                            } else {

                                JSONObject json = new JSONObject(jsonString);

                                String errcode = json.getString("errcode");
                                if (!errcode.equals("0")) {
                                    String errmsg = json.getString("errmsg");

                                    errcode_map.put("errcode", errcode);
                                    errcode_map.put("errmsg", errmsg);

                                    Message message2 = Message.obtain();

                                    message2.obj = errcode_map;

                                    null_handler.sendMessage(message2);

                                } else {

                                    JSONObject data = json.getJSONObject("data");

                                    PayReq req = new PayReq();
                                    // req.appId = "wxf8b4f85f3a794e77"; // 测试用appId
                                    req.appId = data.getString("appid");
                                    req.partnerId = data.getString("partnerid");

                                    req.packageValue = data.getString("package");


                                    req.nonceStr = data.getString("noncestr");
                                    req.prepayId = data.getString("prepayid");

                                    req.timeStamp = data.getString("timestamp");

                                    Log.d("44444", req.timeStamp);

                                    req.sign = data.getString("sign");
                                    req.extData = "app data"; // optional


                                    Message message = Message.obtain();

                                    message.obj = req;
                                    payhandler.sendMessage(message);

                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });


    }



    public class PriceAdapter extends BaseAdapter {

        Context context = null;
        private LayoutInflater layoutInflater;
        private List<Map<String, String>> list = null;

        public PriceAdapter(Context context) {
            this.context = context;
            layoutInflater = LayoutInflater.from(context);
        }

        public void setData(List<Map<String, String>> list) {
            this.list = list;
        }

        @Override
        public int getCount() {
            // TODO 自动生成的方法存根
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO 自动生成的方法存根
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO 自动生成的方法存根
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = null;

            if (convertView == null) {
                view = layoutInflater.inflate(R.layout.policy_price_item, null);
            } else {
                view = convertView;
            }

            TextView p_name = (TextView) view.findViewById(R.id.p_name);
            TextView p_price = (TextView) view.findViewById(R.id.p_price);

            Log.d("44444", list.get(position).get("policy_name"));
            p_name.setText(list.get(position).get("policy_name"));

            p_price.setText(list.get(position).get("policy_price"));

            return view;

        }

    }

    public class HolderAdapter extends BaseAdapter {

        Context context = null;
        private LayoutInflater layoutInflater;
        private List<Map<String, String>> list = null;

        public HolderAdapter(Context context) {
            this.context = context;
            layoutInflater = LayoutInflater.from(context);
        }

        public void setData(List<Map<String, String>> list) {
            this.list = list;
        }

        @Override
        public int getCount() {
            // TODO 自动生成的方法存根
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO 自动生成的方法存根
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO 自动生成的方法存根
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = null;

            if (convertView == null) {
                view = layoutInflater.inflate(R.layout.policy_price_item, null);
            } else {
                view = convertView;
            }

            TextView p_name = (TextView) view.findViewById(R.id.p_name);
            TextView p_price = (TextView) view.findViewById(R.id.p_price);

            p_name.setText(list.get(position).get("holder_title"));

            p_price.setText(list.get(position).get("holder_value"));

            return view;

        }

    }

    public class InsuredAdapter extends BaseAdapter {

        Context context = null;
        private LayoutInflater layoutInflater;
        private List<Map<String, String>> list = null;

        public InsuredAdapter(Context context) {
            this.context = context;
            layoutInflater = LayoutInflater.from(context);
        }

        public void setData(List<Map<String, String>> list) {
            this.list = list;
        }

        @Override
        public int getCount() {
            // TODO 自动生成的方法存根
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO 自动生成的方法存根
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO 自动生成的方法存根
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = null;

            if (convertView == null) {
                view = layoutInflater.inflate(R.layout.policy_price_item, null);
            } else {
                view = convertView;
            }

            TextView p_name = (TextView) view.findViewById(R.id.p_name);
            TextView p_price = (TextView) view.findViewById(R.id.p_price);

            p_name.setText(list.get(position).get("insured_title"));
            p_price.setText(list.get(position).get("insured_value"));

            return view;

        }

    }

    public class OtherAdapter extends BaseAdapter {

        Context context = null;
        private LayoutInflater layoutInflater;
        private List<Map<String, String>> list = null;

        public OtherAdapter(Context context) {
            this.context = context;
            layoutInflater = LayoutInflater.from(context);
        }

        public void setData(List<Map<String, String>> list) {
            this.list = list;
        }

        @Override
        public int getCount() {
            // TODO 自动生成的方法存根
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO 自动生成的方法存根
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO 自动生成的方法存根
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = null;

            if (convertView == null) {
                view = layoutInflater.inflate(R.layout.policy_price_item, null);
            } else {
                view = convertView;
            }

            TextView p_name = (TextView) view.findViewById(R.id.p_name);
            TextView p_price = (TextView) view.findViewById(R.id.p_price);

            p_name.setText(list.get(position).get("other_title"));

            p_price.setText(list.get(position).get("other_value"));

            return view;

        }

    }

    public class BenefitAdapter extends BaseAdapter {

        Context context = null;
        private LayoutInflater layoutInflater;
        private List<Map<String, String>> list = null;

        public BenefitAdapter(Context context) {
            this.context = context;
            layoutInflater = LayoutInflater.from(context);
        }

        public void setData(List<Map<String, String>> list) {
            this.list = list;
        }

        @Override
        public int getCount() {
            // TODO 自动生成的方法存根
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO 自动生成的方法存根
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO 自动生成的方法存根
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = null;

            if (convertView == null) {
                view = layoutInflater.inflate(R.layout.policy_price_item, null);
            } else {
                view = convertView;
            }

            TextView p_name = (TextView) view.findViewById(R.id.p_name);
            TextView p_price = (TextView) view.findViewById(R.id.p_price);

            p_name.setText(list.get(position).get("benefit_title"));

            p_price.setText(list.get(position).get("benefit_value"));

            return view;

        }

    }


}
