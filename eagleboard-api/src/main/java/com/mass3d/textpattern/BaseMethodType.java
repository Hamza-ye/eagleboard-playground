package com.mass3d.textpattern;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class BaseMethodType
    implements MethodType {

  private Pattern pattern;

  BaseMethodType(Pattern pattern) {
    this.pattern = pattern;
  }

  @Override
  public boolean validatePattern(String raw) {
    return pattern.matcher(raw).matches();
  }

  @Override
  public boolean validateText(String format, String text) {
    return Pattern.compile(getValueRegex(format)).matcher(text).matches();
  }

  @Override
  public String getParam(String raw) {
    Matcher m = pattern.matcher(raw);

    if (m.matches()) {
      return m.group(1);
    }

    return null;
  }

  @Override
  public String getFormattedText(String format, String value) {
    return value;
  }
}
