package com.cozs.qrcode.module.library;

import android.content.Context;
import android.content.res.Resources;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.Utils;
import com.cozs.qrcode.module.bean.LocalInfoBean;
import com.cozs.qrcode.module.net.NetService;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LocalLibrary {
    private static final String TAG = "LocalLibrary";

    private volatile static LocalLibrary instance;

    private LocalInfoBean localInfoBean;

    private LocalLibrary() {}

    public static LocalLibrary getInstance() {
        if (instance == null) {
            synchronized (LocalLibrary.class) {
                if (instance == null) {
                    instance = new LocalLibrary();
                }
            }
        }
        return instance;
    }

    public String getLocalIp() {
        if (localInfoBean != null && !TextUtils.isEmpty(localInfoBean.getIp())) {
            return localInfoBean.getIp();
        }
        return NetworkUtils.getIPAddress(true);
    }

    public String getLocalCountryCode() {
        if (localInfoBean != null && !TextUtils.isEmpty(localInfoBean.getCountryCode())) {
            return localInfoBean.getCountryCode();
        }
        String ctyCode = getCountryCodeBySim();
        return TextUtils.isEmpty(ctyCode) ? getCountryCodeByLanguage() : ctyCode;
    }

    public String getLocalCountryCodeFromSim() {
        return getCountryCodeBySim();
    }

    public void requestLocalInfo() {
        NetService.getInstance().requestLocalInfo(new Callback<LocalInfoBean>() {
            @Override
            public void onResponse(@NonNull Call<LocalInfoBean> call, @NonNull Response<LocalInfoBean> response) {
                localInfoBean = response.body();
                Logger.e(TAG, "--> requestLocalInfo() onSuccess  localInfoBean=" + localInfoBean);
            }

            @Override
            public void onFailure(@NonNull Call<LocalInfoBean> call, @NonNull Throwable t) {
                Logger.e(TAG, "--> requestLocalInfo() onFailure: " + t);
            }
        });
    }

    /**
     * Return the country code by sim card.
     *
     * @param defaultValue The default value.
     * @return the country code
     */
    public static String getCountryNumBySim(String defaultValue) {
        String code = countryCodeMap.get(getCountryCodeBySim());
        if (code == null) {
            return defaultValue;
        }
        return code;
    }

    /**
     * Return the country by sim card.
     *
     * @return the country
     */
    private static String getCountryCodeBySim() {
        TelephonyManager manager = (TelephonyManager) Utils.getApp().getSystemService(Context.TELEPHONY_SERVICE);
        if (manager != null) {
            return manager.getSimCountryIso().toUpperCase();
        }
        return "";
    }

    /**
     * Return the country by system language.
     *
     * @return the country
     */
    private static String getCountryCodeByLanguage() {
        return Resources.getSystem().getConfiguration().locale.getCountry();
    }

    private static final Map<String, String> countryCodeMap = new HashMap<String, String>() {{
        put("AL", "+355");
        put("DZ", "+213");
        put("AF", "+93");
        put("AR", "+54");
        put("AE", "+971");
        put("AW", "+297");
        put("OM", "+968");
        put("AZ", "+994");
        put("AC", "+247");
        put("EG", "+20");
        put("ET", "+251");
        put("IE", "+353");
        put("EE", "+372");
        put("AD", "+376");
        put("AO", "+244");
        put("AI", "+1");
        put("AG", "+1");
        put("AT", "+43");
        put("AX", "+358");
        put("AU", "+61");
        put("BB", "+1");
        put("PG", "+675");
        put("BS", "+1");
        put("PK", "+92");
        put("PY", "+595");
        put("PS", "+970");
        put("BH", "+973");
        put("PA", "+507");
        put("BR", "+55");
        put("BY", "+375");
        put("BM", "+1");
        put("BG", "+359");
        put("MP", "+1");
        put("BJ", "+229");
        put("BE", "+32");
        put("IS", "+354");
        put("PR", "+1");
        put("PL", "+48");
        put("BA", "+387");
        put("BO", "+591");
        put("BZ", "+501");
        put("BW", "+267");
        put("BT", "+975");
        put("BF", "+226");
        put("BI", "+257");
        put("KP", "+850");
        put("GQ", "+240");
        put("DK", "+45");
        put("DE", "+49");
        put("TL", "+670");
        put("TG", "+228");
        put("DO", "+1");
        put("DM", "+1");
        put("RU", "+7");
        put("EC", "+593");
        put("ER", "+291");
        put("FR", "+33");
        put("FO", "+298");
        put("PF", "+689");
        put("GF", "+594");
        put("VA", "+39");
        put("PH", "+63");
        put("FJ", "+679");
        put("FI", "+358");
        put("CV", "+238");
        put("FK", "+500");
        put("GM", "+220");
        put("CG", "+242");
        put("CD", "+243");
        put("CO", "+57");
        put("CR", "+506");
        put("GG", "+44");
        put("GD", "+1");
        put("GL", "+299");
        put("GE", "+995");
        put("CU", "+53");
        put("GP", "+590");
        put("GU", "+1");
        put("GY", "+592");
        put("KZ", "+7");
        put("HT", "+509");
        put("KR", "+82");
        put("NL", "+31");
        put("BQ", "+599");
        put("SX", "+1");
        put("ME", "+382");
        put("HN", "+504");
        put("KI", "+686");
        put("DJ", "+253");
        put("KG", "+996");
        put("GN", "+224");
        put("GW", "+245");
        put("CA", "+1");
        put("GH", "+233");
        put("GA", "+241");
        put("KH", "+855");
        put("CZ", "+420");
        put("ZW", "+263");
        put("CM", "+237");
        put("QA", "+974");
        put("KY", "+1");
        put("CC", "+61");
        put("KM", "+269");
        put("XK", "+383");
        put("CI", "+225");
        put("KW", "+965");
        put("HR", "+385");
        put("KE", "+254");
        put("CK", "+682");
        put("CW", "+599");
        put("LV", "+371");
        put("LS", "+266");
        put("LA", "+856");
        put("LB", "+961");
        put("LT", "+370");
        put("LR", "+231");
        put("LY", "+218");
        put("LI", "+423");
        put("RE", "+262");
        put("LU", "+352");
        put("RW", "+250");
        put("RO", "+40");
        put("MG", "+261");
        put("IM", "+44");
        put("MV", "+960");
        put("MT", "+356");
        put("MW", "+265");
        put("MY", "+60");
        put("ML", "+223");
        put("MK", "+389");
        put("MH", "+692");
        put("MQ", "+596");
        put("YT", "+262");
        put("MU", "+230");
        put("MR", "+222");
        put("US", "+1");
        put("AS", "+1");
        put("VI", "+1");
        put("MN", "+976");
        put("MS", "+1");
        put("BD", "+880");
        put("PE", "+51");
        put("FM", "+691");
        put("MM", "+95");
        put("MD", "+373");
        put("MA", "+212");
        put("MC", "+377");
        put("MZ", "+258");
        put("MX", "+52");
        put("NA", "+264");
        put("ZA", "+27");
        put("SS", "+211");
        put("NR", "+674");
        put("NI", "+505");
        put("NP", "+977");
        put("NE", "+227");
        put("NG", "+234");
        put("NU", "+683");
        put("NO", "+47");
        put("NF", "+672");
        put("PW", "+680");
        put("PT", "+351");
        put("JP", "+81");
        put("SE", "+46");
        put("CH", "+41");
        put("SV", "+503");
        put("WS", "+685");
        put("RS", "+381");
        put("SL", "+232");
        put("SN", "+221");
        put("CY", "+357");
        put("SC", "+248");
        put("SA", "+966");
        put("BL", "+590");
        put("CX", "+61");
        put("ST", "+239");
        put("SH", "+290");
        put("PN", "+870");
        put("KN", "+1");
        put("LC", "+1");
        put("MF", "+590");
        put("SM", "+378");
        put("PM", "+508");
        put("VC", "+1");
        put("LK", "+94");
        put("SK", "+421");
        put("SI", "+386");
        put("SJ", "+47");
        put("SZ", "+268");
        put("SD", "+249");
        put("SR", "+597");
        put("SB", "+677");
        put("SO", "+252");
        put("TJ", "+992");
        put("TH", "+66");
        put("TZ", "+255");
        put("TO", "+676");
        put("TC", "+1");
        put("TA", "+290");
        put("TT", "+1");
        put("TN", "+216");
        put("TV", "+688");
        put("TR", "+90");
        put("TM", "+993");
        put("TK", "+690");
        put("WF", "+681");
        put("VU", "+678");
        put("GT", "+502");
        put("VE", "+58");
        put("BN", "+673");
        put("UG", "+256");
        put("UA", "+380");
        put("UY", "+598");
        put("UZ", "+998");
        put("GR", "+30");
        put("ES", "+34");
        put("EH", "+212");
        put("SG", "+65");
        put("NC", "+687");
        put("NZ", "+64");
        put("HU", "+36");
        put("SY", "+963");
        put("JM", "+1");
        put("AM", "+374");
        put("YE", "+967");
        put("IQ", "+964");
        put("UM", "+1");
        put("IR", "+98");
        put("IL", "+972");
        put("IT", "+39");
        put("IN", "+91");
        put("ID", "+62");
        put("GB", "+44");
        put("VG", "+1");
        put("IO", "+246");
        put("JO", "+962");
        put("VN", "+84");
        put("ZM", "+260");
        put("JE", "+44");
        put("TD", "+235");
        put("GI", "+350");
        put("CL", "+56");
        put("CF", "+236");
        put("CN", "+86");
        put("MO", "+853");
        put("TW", "+886");
        put("HK", "+852");
    }};
}
