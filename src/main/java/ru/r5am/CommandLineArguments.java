package ru.r5am;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.kohsuke.args4j.Option;

import java.io.Serializable;

/**
 * Описание ожидаемых аргументов командной строки
 * Created by Zoer on 15.08.2016.
 * @author  Aleksandr Jashhuk (Zoer), r5am@mail.ru
 */
class CommandLineArguments implements Serializable {

    @Option(name = "-t",
            aliases = "--testnumber",
            usage = "The number of the running test.",
            required = true          // Обязательный параметр
    )
    String testNumber;

    @Option(name = "-p",            // Короткий вариант
            aliases = "--prod",     // длинный вариант
            usage = "To use a configuration file for the product server."
    )
    boolean production;

    @Option(name = "-ff",
            aliases = "--firefox",
            usage = "Use Firefox instead of Chrome by default."
    )
    boolean firefox;

    @Option(name = "-r",
            aliases = "--resolution",
            usage = "Resolution, browser window size, for example '800,600'."
    )
    String browserResolution;

    // все остальные неописанные аргументы
//    @Argument
//    List<String> extraArgs = new ArrayList<>();

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }

}
