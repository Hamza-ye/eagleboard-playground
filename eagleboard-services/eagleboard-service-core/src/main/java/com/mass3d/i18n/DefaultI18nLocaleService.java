package com.mass3d.i18n;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.annotation.PostConstruct;
import com.mass3d.common.BaseIdentifiableObject;
import com.mass3d.common.IdentifiableObject;
import com.mass3d.common.comparator.LocaleNameComparator;
import com.mass3d.i18n.locale.I18nLocale;
import com.mass3d.system.util.LocaleUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service( "com.mass3d.i18n.118nLocaleService" )
@Transactional
public class DefaultI18nLocaleService
    implements I18nLocaleService {
  // -------------------------------------------------------------------------
  // Dependencies
  // -------------------------------------------------------------------------

  private I18nLocaleStore localeStore;
  private Map<String, String> languages = new LinkedHashMap<>();
  private Map<String, String> countries = new LinkedHashMap<>();

  public void setLocaleStore(I18nLocaleStore localeStore) {
    this.localeStore = localeStore;
  }

  /**
   * Load all ISO languages and countries into mappings.
   */
  @PostConstruct
  public void init() {
    List<IdentifiableObject> langs = new ArrayList<>();
    List<IdentifiableObject> countrs = new ArrayList<>();

    for (String lang : Locale.getISOLanguages()) {
      langs.add(new BaseIdentifiableObject(lang, lang, new Locale(lang).getDisplayLanguage()));
    }

    for (String country : Locale.getISOCountries()) {
      countrs.add(new BaseIdentifiableObject(country, country,
          new Locale("en", country).getDisplayCountry()));
    }

    Collections.sort(langs);
    Collections.sort(countrs);

    for (IdentifiableObject lang : langs) {
      languages.put(lang.getCode(), lang.getName());
    }

    for (IdentifiableObject countr : countrs) {
      countries.put(countr.getCode(), countr.getName());
    }
  }

  // -------------------------------------------------------------------------
  // I18nLocaleService implementation
  // -------------------------------------------------------------------------

  @Override
  public Map<String, String> getAvailableLanguages() {
    return languages;
  }

  @Override
  public Map<String, String> getAvailableCountries() {
    return countries;
  }

  @Override
  public I18nLocale addI18nLocale(String language, String country) {
    String languageName = languages.get(language);
    String countryName = countries.get(country);

    if (language == null || languageName == null) {
      throw new IllegalArgumentException("Invalid Language.");
    }

    if (country != null && countryName == null) {
      throw new IllegalArgumentException("Invalid country.");
    }

    String localeStr = LocaleUtils.getLocaleString(language, country, null);
    Locale locale = LocaleUtils.getLocale(localeStr);

    I18nLocale i18nLocale = new I18nLocale(locale);

    saveI18nLocale(i18nLocale);

    return i18nLocale;
  }

  @Override
  public void saveI18nLocale(I18nLocale locale) {
    localeStore.save(locale);
  }

  @Override
  public I18nLocale getI18nLocale(Long id) {
    return localeStore.get(id);
  }

  @Override
  public I18nLocale getI18nLocaleByUid(String uid) {
    return localeStore.getByUid(uid);
  }

  @Override
  public I18nLocale getI18nLocale(Locale locale) {
    return localeStore.getI18nLocaleByLocale(locale);
  }

  @Override
  public void deleteI18nLocale(I18nLocale locale) {
    localeStore.delete(locale);
  }

  @Override
  public int getI18nLocaleCount() {
    return localeStore.getCount();
  }

  @Override
  public List<Locale> getAllLocales() {
    List<Locale> locales = new ArrayList<>();

    for (I18nLocale locale : localeStore.getAll()) {
      locales.add(LocaleUtils.getLocale(locale.getLocale()));
    }

    Collections.sort(locales, LocaleNameComparator.INSTANCE);

    return locales;
  }

  @Override
  public List<I18nLocale> getAllI18nLocales() {
    return localeStore.getAll();
  }


}
