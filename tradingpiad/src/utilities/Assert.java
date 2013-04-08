package utilities;

public class Assert {
	
	public static void nullCheck(Object... objects){
		if(isNull(objects))
			throw new IllegalArgumentException("Null parameter forbidden");
	}
	
	public static boolean isNull(Object... objects){
		for (Object o:objects)
			if(o==null)
				return true;
		return false;
	}
	
	public static void checkPrecond(boolean b,String msg){
		if (!b)
			throw new IllegalArgumentException("Precondition(s) incorrect(s):"+msg);
	}
	
	public static void checkPostcond(boolean b,String msg){
		if (!b)
			throw new IllegalStateException("Postcondition(s) incorrect(s):"+msg);
	}

}
