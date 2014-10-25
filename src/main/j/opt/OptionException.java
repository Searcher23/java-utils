/**
 * @author Lucas Tan
 */

package j.opt;

/**
 * An immutable class representing a generic option 
 * exception.
 */
@SuppressWarnings("serial")
public class OptionException extends Exception
{
    public OptionException(String msg)
    {
        this(msg,null);
    }

    public OptionException(String msg, Throwable cause)
    {
        super(msg, cause);
    }
}

