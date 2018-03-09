package de.fzj.peerpub.utils;

import java.security.SecureRandom;
import java.lang.StringBuilder;

public class Random {
  static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
  static SecureRandom rnd = new SecureRandom();

  public static String getString( int len ){
     StringBuilder sb = new StringBuilder( len );
     for( int i = 0; i < len; i++ )
        sb.append( AB.charAt( rnd.nextInt(AB.length()) ) );
     return sb.toString();
  }

  public static int getInt() {
    return rnd.nextInt(32);
  }
  public static int getInt(int range) {
    return rnd.nextInt(range);
  }
  public static boolean getBool() {
    return rnd.nextInt(1)==0;
  }
}
