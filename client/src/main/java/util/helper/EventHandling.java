package util.helper;

import actions.MouseAction;

public class EventHandling {

	public static <T> T getTargetAs(MouseAction ma, Class< ? extends T> clazz) {
		
		Object o = null;
		
		if(ma.getTarget() != null) {
		
			o = ma.getTarget();
		
		} else if (ma.getGi().getEnvId().equals(ma.getTargetId())) {
		
			o = ma.getGi();
		
		} else if(ma.getTargetId() != null) {
			
			o = ma.getGi().getDirectTargetChildren(ma);
			
		}
		
		return clazz.isInstance(o) ? clazz.cast(o) : null;
	
	}
}
