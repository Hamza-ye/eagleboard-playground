package com.mass3d.startup;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableSet;
import java.util.Locale;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.mass3d.i18n.I18nLocaleService;
import com.mass3d.i18n.locale.I18nLocale;
import com.mass3d.system.startup.TransactionContextStartupRoutine;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Populates default I18nLocales if none exists.
 *
 */
public class I18nLocalePopulator
    extends TransactionContextStartupRoutine
{
    private static final Log log = LogFactory.getLog( I18nLocalePopulator.class );

//    @Autowired
    private I18nLocaleService localeService;

    private static final ImmutableSet<String> DEFAULT_LOCALES = ImmutableSet.of(
        "af","ar","bi","am","de","dz","en","es","fa","fr","gu","hi","id","it",
        "km","lo","my","ne","nl","no","ps","pt","ru","rw","sw","tg","vi","zh" );

    public I18nLocalePopulator( I18nLocaleService localeService )
    {
        checkNotNull( localeService );

        this.localeService = localeService;
    }

    @Override
    public void executeInTransaction()
    {
        int count = localeService.getI18nLocaleCount();

        if ( count > 0 )
        {
            return;
        }

        for ( String locale : DEFAULT_LOCALES )
        {
            localeService.saveI18nLocale( new I18nLocale( new Locale( locale ) ) );
        }

        log.info( "Populated default locales" );
    }
}
