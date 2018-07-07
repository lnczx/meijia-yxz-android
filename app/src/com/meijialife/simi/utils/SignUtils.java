package com.meijialife.simi.utils;

import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import com.meijia.utils.StringUtil;

/**
 * 传递参数签名类
 * @author lnczx
 * 说明:
 * 采用参数进行加密签名，参考的是
 * 
 * 签名算法
 *	（签名校验工具）
 *	签名生成的通用步骤如下：
 *	第一步，设所有发送或者接收到的数据为集合M，将集合M内非空参数值的参数按照参数名ASCII码从小到大排序（字典序），使用URL键值对的格式（即key1=value1&key2=value2…）拼接成字符串stringA。
 *	特别注意以下重要规则：
 *	◆ 参数名ASCII码从小到大排序（字典序）；
 *	◆ 如果参数的值为空不参与签名；
 *	◆ 参数名区分大小写；
 *	第二步，在stringA最后拼接上key得到stringSignTemp字符串，并对stringSignTemp进行MD5运算，再将得到的字符串所有字符转换为大写，得到sign值signValue。
 *  
 *  举例=======================
 *  1.假设传送的参数如下：
 *      appkey 		: 075eb27b402246eab4da3fd429f60931
 *      nonce_str	: ibuaiVcKdpRxkhJA
 *  		mobile 		: 18612514665
 *  		token  		: 1234
 *  2.对参数按照key=value的格式，并按照参数名ASCII字典序排序如下：
 *      String signStr = "appkey=075eb27b402246eab4da3fd429f60931&mobile=18612514665&nonce_str=ibuaiVcKdpRxkhJA&token=1234"
 *  3.拼接API密钥：
 *      signStr = signStr + "&secret=8f35e1b5afc54784a0e6db6f25d09cdc";
 *  4.采用Md5加密之后转换为大写
 *      String sign = md5(stringSignTemp).toUpperCase();
 *  5.最后传递参数也带上sign
 *      appkey=075eb27b402246eab4da3fd429f60931&mobile=18612514665&nonce_str=ibuaiVcKdpRxkhJA&token=1234&sign=xxxxx
 *  
 */
public class SignUtils {
	public static String map2String(SortedMap<String, String> map) {
		StringBuilder builder = new StringBuilder();
		if (map.size() == 0) {
			return "";
		}
		for (Entry<String, String> kv : map.entrySet()) {
//				builder.append(kv.getKey() + "=" + URLEncoder.encode(kv.getValue(), "UTF-8") + "&");
			if (!StringUtil.isEmpty(kv.getValue())) builder.append(kv.getKey() + "=" + kv.getValue() + "&");
		}

		if (builder.length() > 0) {
			return builder.substring(0, builder.length() - 1);
		} else {
			return "";
		}
	}

	private static String md5(String str) {
		MessageDigest messageDigest = null;
		try {
			messageDigest = MessageDigest.getInstance("MD5");
			messageDigest.reset();
			messageDigest.update(str.getBytes("UTF-8"));
			byte[] byteArray = messageDigest.digest();
			StringBuffer md5StrBuff = new StringBuffer();
			for (int i = 0; i < byteArray.length; i++) {
				if (Integer.toHexString(0xFF & byteArray[i]).length() == 1)
					md5StrBuff.append("0").append(Integer.toHexString(0xFF & byteArray[i]));
				else
					md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
			}
			return md5StrBuff.toString();
		} catch (Exception e) {
			throw new RuntimeException("md5 failed!");
		}
	}

	private static void dumpParams(Map<String, String[]> params) {
		for (Entry<String, String[]> kv : params.entrySet()) {
			System.out.println(kv.getKey() + "=" + kv.getValue()[0]);
		}

	}

	public static boolean verifySign(HttpServletRequest request, String appkey, String appsecret) {
		SortedMap<String, String> map = new TreeMap<String, String>();
		Map<String, String[]> params = request.getParameterMap();

		// for debug only
//		dumpParams(params);

		String expSign = null;
		String[] appkeyParam = params.get("appkey");
		if (appkeyParam == null) {
			return false;
		}
		if (!appkey.equals(appkeyParam[0])) {
			return false;
		}
		for (Entry<String, String[]> pv : params.entrySet()) {
			String param = pv.getKey();
			String[] value = pv.getValue();
			if (!param.equals("sign")) {
				map.put(param, value[0]);
			} else {
				expSign = value[0];
			}
		}
		String string1 = map2String(map);
		String stringSignTemp = string1 + "&secret=" + appsecret;
		String actualSign = md5(stringSignTemp).toUpperCase();

		return expSign.equals(actualSign);
	}

	public static String getUniqueId() {
		return UUID.randomUUID().toString().replaceAll("-", "");
	}

	public static String createSign(SortedMap<String, String> map, String appsecret) {
		String string1 = map2String(map);
		String stringSignTemp = string1 + "&secret=" + appsecret;
		return md5(stringSignTemp).toUpperCase();
	}

	public static String sign(SortedMap<String, String> map, String appkey, String appsecret) {
		String oncestr = getUniqueId();

		map.put("appkey", appkey);
		map.put("oncestr", oncestr);
		String sign = createSign(map, appsecret);
		map.put("sign", sign);

		return map2String(map);
	}
	

	public static HashMap<String, String> getSignParams(HashMap<String, String> params) {
		String oncestr = getUniqueId();
		SortedMap<String, String> map = SignUtils.ConvertToSortMap(params);
		map.put("appkey", SignConfig.appkey);
		map.put("oncestr", oncestr);
		String sign = SignUtils.createSign(map, SignConfig.appsecret);
		map.put("sign", sign);
		params = SignUtils.ConvertToMap(map);
		return params;
	}
	
	@SuppressWarnings("rawtypes")
	public static SortedMap<String, String>  ConvertToSortMap(HashMap<String, String> params) {
		SortedMap<String, String> map = new TreeMap<String, String>();
		Iterator it = params.keySet().iterator();
        while (it.hasNext()) {
            String key = it.next().toString();
            String value = params.get(key);
			map.put(key, value);
        }
		return map;
	}
	
	@SuppressWarnings("rawtypes")
	public static HashMap<String, String>  ConvertToMap(SortedMap<String, String> map) {
		HashMap<String, String> params = new HashMap<String, String>();
		Iterator it = map.keySet().iterator();
        while (it.hasNext()) {
            String key = it.next().toString();
            String value = map.get(key);
			params.put(key, value);
        }
		return params;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
//		System.out.println(JhjSignUtils.getUniqueId());
		
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("mobile", "18612514665");
		params.put("token", "1234");
		params = SignUtils.getSignParams(params);
		System.out.println(params);
	}
}
