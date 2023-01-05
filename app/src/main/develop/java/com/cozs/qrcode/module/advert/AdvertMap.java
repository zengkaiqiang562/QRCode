package com.cozs.qrcode.module.advert;

import com.cozs.qrcode.module.constant.AdvertPlace;
import com.cozs.qrcode.module.constant.AdvertState;
import com.cozs.qrcode.module.library.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

class AdvertMap {

    private static final String TAG = "AdvertMap";

    private static final Map<AdvertPlace, List<AdvertResource<?>>> map = new ConcurrentHashMap<>();

    static synchronized void put(AdvertPlace adPlaceHolder, AdvertResource<?> advertResource) {
        List<AdvertResource<?>> advertResources = map.get(adPlaceHolder);
        if (advertResources == null) {
            advertResources = new ArrayList<>();
            map.put(adPlaceHolder, advertResources);
        }
        advertResources.add(advertResource);
    }

    // 获取广告位上指定状态的广告缓存
    static synchronized AdvertResource<?> get(AdvertPlace adPlaceHolder, AdvertState adStatus) {
        List<AdvertResource<?>> advertResources = map.get(adPlaceHolder);
        if (advertResources == null || advertResources.size() <= 0) {
            return null;
        }
        for (int i = advertResources.size() - 1; i >= 0; i--) {
            AdvertResource<?> advertResource = advertResources.get(i);
            if (advertResource.getAdStatus() == adStatus) { //
                Logger.e(TAG, "--> get()  adPlaceHolder=" + adPlaceHolder + "  adStatus=" + adStatus);
                return advertResource;
            }
        }

        return null;
    }

    // 移除广告位上指定状态的广告缓存
    static synchronized void remove(AdvertPlace adPlaceHolder, AdvertState adStatus) {
        List<AdvertResource<?>> advertResources = map.get(adPlaceHolder);
        if (advertResources == null || advertResources.size() <= 0) {
            return;
        }

        for (int i = advertResources.size() - 1; i >= 0; i--) {
            AdvertResource<?> advertResource = advertResources.get(i);
            if (advertResource.getAdStatus() == adStatus) {
                advertResources.remove(advertResource);
            }
        }
    }

//    // 获取广告位上缓存的广告类型
//    static synchronized AdCategory getAdCategory(AdPlaceHolder adPlaceHolder) {
//        List<AdPacket<?>> adPackets = map.get(adPlaceHolder);
//        if (adPackets == null || adPackets.size() <= 0) {
//            return null;
//        }
//        AdCategory adCategory = AdCategory.convert(adPackets.get(0).getTypeEntity().getCategory());
//        Logger.e(TAG, "--> getAdCategory()  adPlaceHolder=" + adPlaceHolder + "  adCategory=" + adCategory);
//        return type;
//    }

    static synchronized Set<Map.Entry<AdvertPlace, List<AdvertResource<?>>>> entrySet() {
        return map.entrySet();
    }
}
