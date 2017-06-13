package com.ajeet.util;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.netty.util.CharsetUtil;

/**
 * Splits an HTTP query string into a path string and key-value parameter pairs. This decoder is for
 * one time use only. Create a new instance for each URI:
 * 
 * <pre>
 * {@link QueryStringDecoder} decoder = new {@link QueryStringDecoder}("/hello?recipient=world&x=1;y=2");
 * assert decoder.getPath().equals("/hello");
 * assert decoder.getParameters().get("recipient").equals("world");
 * assert decoder.getParameters().get("x").equals("1");
 * assert decoder.getParameters().get("y").equals("2");
 * </pre>
 * 
 */

/**
 * QueryStringDecoder class present in netty 3.2.0 does not split query parameters seperated by ";".
 * So, copying this class from netty version 3.2.11 which takes care of splitting query parameters
 * separated by both & and ; If we upgrade the netty version, we will directly use netty class and
 * discard this copy
 * 
 */
public class QueryStringDecoder {

  private final Charset charset;
  private final String uri;
  private String path;
  private Map<String, String> params;

 

  // Regex to find last ('&' or ';')
  private static final Pattern name_pattern = Pattern.compile("[;&](?!.*[;&])");

  // Regex to find substring from first character to last ('&' or ';')
  private static final Pattern value_pattern = Pattern.compile("^.*[;&]");

  // Regex to find trailing ('&' or ';')
  private static final Pattern trailing_separator_pattern = Pattern.compile("[;&]*$");


  // we don't support following query params in Chocolate
  private String[] Params_to_Discard = {"DIMSIZE;", "PUBCLK;", "PUBENDTRK;", "DIMSIZE&", "PUBCLK&",
      "PUBENDTRK&"};

  /**
   * Creates a new decoder that decodes the specified URI. The decoder will assume that the query
   * string is encoded in UTF-8.
   */
  public QueryStringDecoder(String uri) {
    this(uri, CharsetUtil.UTF_8);
  }

  /**
   * Creates a new decoder that decodes the specified URI encoded in the specified charset.
   */
  public QueryStringDecoder(String uri, Charset charset) {
    if (uri == null) {
      throw new NullPointerException("uri");
    }
    if (charset == null) {
      throw new NullPointerException("charset");
    }

    this.charset = charset;
    this.uri = uri;
  }

  /**
   * @deprecated Use {@link #QueryStringDecoder(String, Charset)} instead.
   */
  @Deprecated
  public QueryStringDecoder(String uri, String charset) {
    this(uri, Charset.forName(charset));
  }

  /**
   * Creates a new decoder that decodes the specified URI. The decoder will assume that the query
   * string is encoded in UTF-8.
   */
  public QueryStringDecoder(URI uri) {
    this(uri, CharsetUtil.UTF_8);
  }

  /**
   * Creates a new decoder that decodes the specified URI encoded in the specified charset.
   */
  public QueryStringDecoder(URI uri, Charset charset) {
    if (uri == null) {
      throw new NullPointerException("uri");
    }
    if (charset == null) {
      throw new NullPointerException("charset");
    }

    this.charset = charset;
    this.uri = uri.toASCIIString();
  }

  /**
   * @deprecated Use {@link #QueryStringDecoder(URI, Charset)} instead.
   */
  @Deprecated
  public QueryStringDecoder(URI uri, String charset) {
    this(uri, Charset.forName(charset));
  }

  /**
   * Returns the decoded path string of the URI.
   */
  public String getPath() {
    if (path == null) {
      int pathEndPos = uri.indexOf('?');
      if (pathEndPos < 0) {
        path = uri;
      } else {
        return path = uri.substring(0, pathEndPos);
      }
    }
    return path;
  }

  /**
   * Returns the decoded key-value parameter pairs of the URI.
   */
  public Map<String, String> getParameters() {
    if (params == null) {
      int pathLength = getPath().length();
      if (uri.length() == pathLength) {
        return Collections.emptyMap();
      }
      params = decodeParams(uri.substring(pathLength + 1));
    }
    return params;
  }

  /**
   * @param s
   * @return
   */
  private String cleanQueryString(String s) {
    for (String p : Params_to_Discard)
      s = s.replace(p, "");
    return s + ";";
  }

 /**
  * Function to populate hashmap of query parameters from URI String of ad call.
  * @param s URI string of ad call
  * @return Map<String, String>
  */
  private Map<String, String> decodeParams(String s) {
    s = cleanQueryString(s);
    String[] substr = s.split("=");
    Matcher m, m2;

    // LinkedHashMap is not required as, order of parameter does not matter.
    Map<String, String> params = new HashMap<String, String>();
    String name = null;
    String value = null;

    for (int i = 0; i < substr.length; i++) {
      String part = substr[i];
      m = value_pattern.matcher(part);
      while (m.find()) {
        value = part.substring(0, m.end() - 1);
        m2 = trailing_separator_pattern.matcher(value);
        m2.find();
        value = value.substring(0, m2.start());
      }

      if (value != null) {
    	  // Double check 
    	  String tmpV = "";
    	  m = value_pattern.matcher(value);
          while (m.find()) {
        	  tmpV = value.substring(0, m.end() - 1);
            m2 = trailing_separator_pattern.matcher(tmpV);
            m2.find();
            value = tmpV.substring(0, m2.start());
          }
        value = value.trim();
      }

      if (name != null) {
        // If multiple values are sent for a parameter, last value will be set.
        addParam(params, decodeComponent(name, charset), decodeComponent(value, charset));
      }
      name = null;

      // so that regex run on first param
      part = ';' + part;

      // find name which is between last ('&' or ';') and '='
      m = name_pattern.matcher(part);
      while (m.find()) {
        name = part.substring(m.end(), part.length());
      }

      value = null;
    }
    return params;
  }

  /**
   * @param s String to decode
   * @param charset
   * @return String
   */
  private static String decodeComponent(String s, Charset charset) {
    if (s == null || s == "") {
      return "";
    }

    try {
      return URLDecoder.decode(s, charset.name());
    } catch (Exception e) {
      return s;
    }
  }

  /**
   * Checks if the value starts/ends with these defined characters/ is null/ is empty.
   * @param value Value passed for a parameter in ad call.
   * @return boolean 
   */
  private static boolean isValidParam(String value) {
    if ((value == null) || value.isEmpty() || value.startsWith("[") || value.endsWith("]")
        || value.startsWith("{") || value.endsWith("}") || value.startsWith("$"))
      return false;

    return true;
  }

  /**
   * This function is to populate valid queryParameters.
   * @param params Map of parameters and their values passed in ad call.
   * @param name   Parameter passed in ad call
   * @param value  Values for the respective parameter.
   * @return void
   */
  private static void addParam(Map<String, String> params, String name, String value) {
    if (isValidParam(value)) {
      params.put(name, value);
    }
  }
}
