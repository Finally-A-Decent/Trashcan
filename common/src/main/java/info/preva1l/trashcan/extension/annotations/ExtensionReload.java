package info.preva1l.trashcan.extension.annotations;

import info.preva1l.trashcan.extension.BaseExtension;
import info.preva1l.trashcan.flavor.annotations.Service;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A method that is annotated with ExtensionReload will get run when {@link BaseExtension#reload()} is called.
 * <p>
 *     <b>The annotated method must either be static or in a {@link Service}</b>
 * </p>
 * <p>
 * Created on 20/05/2025
 *
 * @author Preva1l
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ExtensionReload {
}
