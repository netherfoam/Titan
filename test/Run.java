import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;


public class Run {
	private static final Class<?>[] CLASSES = new Class<?>[]{
		AreaGridTest.class,
		RSCompressionTest.class
	};
	
	public static void main(String[] args){
		for(Class<?> clazz : CLASSES){
			System.out.print(clazz.getName() + ": ");
			Result result = JUnitCore.runClasses(clazz);
			if(result.wasSuccessful()){
				System.out.println("Ok!");
			}
			else{
				System.out.println("Failed!");
				for(Failure f : result.getFailures()){
					System.out.println(f.toString());
				}
			}
		}
	}
}
