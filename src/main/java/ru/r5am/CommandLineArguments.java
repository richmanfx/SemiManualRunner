package ru.r5am;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

// import org.apache.commons.lang3.builder.ToStringBuilder;
// import org.apache.commons.lang3.builder.ToStringStyle;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.Argument;

/**
 * Created by Zoer on 15.08.2016.
 * @author  Aleksandr Jashhuk (Zoer), r5am@mail.ru
 */
class CommandLineArguments implements Serializable {

    @Option(name = "-p",            // Короткий вариант
            aliases = "--prod",     // длинный вариант
            // required = false,       // Необязательный параметр
            usage = "To use a configuration file for the product server."
    )
    boolean production;

    @Option(name = "-ff",
            aliases = "--firefox",
            usage = "Use Firefox instead of Chrome by default."
    )
    boolean firefox;

    // все остальные неописанные аргументы
    @Argument
    List<String> extraArgs = new ArrayList<>();

//    @Override
//    public String toString() {
//        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
//    }

}
