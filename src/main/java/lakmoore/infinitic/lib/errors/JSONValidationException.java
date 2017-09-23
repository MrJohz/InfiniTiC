package lakmoore.infinitic.lib.errors;

public class JSONValidationException extends Exception {
	
	private String reason;
	
	public JSONValidationException(String reason) {
		super(reason);
		this.reason = reason;
	}
	
	public String getReason()  {
		return reason;
	}

	/**
	 * I dunno.  *shrugs*
	 */
	private static final long serialVersionUID = -3247405287417190305L;

}
