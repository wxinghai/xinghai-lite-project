package com.lite.gym.util;

import com.lite.gym.common.LogicalException;
import org.apache.commons.codec.binary.Base64;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.util.Random;
import java.util.UUID;

/**
 *
 * 加密解密工具
 *
 */
public class CodeUtil
{
  private static final char[] HEXES = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

  public static final String EMPTY_STRING = "";


  public CodeUtil() {}




    /**
     * 字符串脱敏
     * @param str
     * @return
     */
  public static String desensitize(String str){
      if(str.length() <2){
          return str;//不处理
      }else if(str.length() ==2){
          return str.substring(0,1)+"*";
      }else if(str.length() <=9){
          int startLen = str.length()/3 ;
          int endLen   = str.length()/3 ;
          int unknowLen= str.length() - startLen - endLen;
          return str.substring(0,startLen)+getStar(unknowLen)
                  +str.substring(str.length()-endLen,str.length());
      }else{
          int startLen = 3 ;
          int endLen   = 3 ;
          int unknowLen= str.length() - startLen - endLen;
          return str.substring(0,startLen)+getStar(unknowLen)
                  +str.substring(str.length()-endLen,str.length());
      }
  }
  private static String getStar(int number){
      StringBuilder star = new StringBuilder();
      for(int i =0 ;i< number;i++){
          star.append("*");
      }
      return star.toString();
  }
  private static final byte[] ROW_BYTES = "80e36e39f34e678c".getBytes();

  public static String trim(String value) {
    return value == null ? null : value.trim(); }

  public static boolean isEmpty(String value)
  {
    int length = value.length();
    if ((value == null) || (length == 0)) {
      return true;
    }
    for (int index = 0; index < length; index++) {
      char ch = value.charAt(index);
      if ((ch != ' ') && (ch != '?') && (!Character.isISOControl(ch)))
      {
        return false;
      }
    }
    return true;
  }


  public static String digest(String content)
    throws Throwable
  {
    if (isEmpty(content)) {
      return content;
    }
    MessageDigest digest = MessageDigest.getInstance("SHA-1");
    byte[] ciphertext = digest.digest(content.getBytes());
    char[] chars = new char[ciphertext.length + ciphertext.length];
    int i = 0;
    for (byte element : ciphertext) {
      chars[(i++)] = HEXES[(element & 0xF)];
      chars[(i++)] = HEXES[(element >> 4 & 0xF)];
    }
    return new String(chars);
  }

  public static String encode(String content)  {
    try{
            if (isEmpty(content)) {
          return content;
        }
        Cipher cipher = Cipher.getInstance("AES");
        SecretKeySpec keySpec = new SecretKeySpec(ROW_BYTES, "AES");
        cipher.init(Cipher.ENCRYPT_MODE, keySpec);
        byte[] ciphertext = cipher.doFinal(content.getBytes());
        return Base64.encodeBase64String(ciphertext);
      }catch (Exception e){
        throw new LogicalException(e.getMessage());
    }
  }

  public static String decode(String content)  {
      try {
          if (isEmpty(content)) {
              return content;
          }
          Cipher cipher = Cipher.getInstance("AES");
          SecretKeySpec keySpec = new SecretKeySpec(ROW_BYTES, "AES");
          cipher.init(Cipher.DECRYPT_MODE, keySpec);
          byte[] ciphertext = cipher.doFinal(Base64.decodeBase64(content));
          return new String(ciphertext);
      }catch (Exception e){
          throw new LogicalException(e.getMessage());
      }
  }

    private static final BCryptPasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder();

    /**
     * 生成随机验证码
     *
     * @param length 长度
     * @return 验证码
     */
    public static String generateRandomCode(int length) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

    /**
     * 生成UUID
     *
     * @return UUID
     */
    public static String generateUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    /**
     * MD5加密
     *
     * @param text 明文
     * @return 密文
     */
    public static String md5(String text) {
        if (!StringUtils.hasText(text)) {
            return null;
        }
        return DigestUtils.md5DigestAsHex(text.getBytes());
    }

    /**
     * 加密密码
     *
     * @param password 明文密码
     * @return 加密后的密码
     */
    public static String encryptPassword(String password) {
        if (!StringUtils.hasText(password)) {
            return null;
        }
        return PASSWORD_ENCODER.encode(password);
    }

    /**
     * 验证密码
     *
     * @param rawPassword     明文密码
     * @param encodedPassword 加密后的密码
     * @return 是否匹配
     */
    public static boolean validatePassword(String rawPassword, String encodedPassword) {
        if (!StringUtils.hasText(rawPassword) || !StringUtils.hasText(encodedPassword)) {
            return false;
        }
        return PASSWORD_ENCODER.matches(rawPassword, encodedPassword);
    }
}
