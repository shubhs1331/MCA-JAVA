package com.brainmentors.los.start;

import com.brainmentors.los.operation.LOSProcess;
import com.brainmentors.los.utils.Utility;

public class Application {

	public static void main(String[] args) {
		LOSProcess process = new LOSProcess();
		while(true) {
		System.out.println("Do you have appplication number or not (Enter 0) Press -1 to exit");
		int applicationNumber=Utility.scanner.nextInt();
		if(applicationNumber==-1)
		{
			System.out.println("Thanks for Using");
			System.exit(0);
		}
		if(applicationNumber==0) {
			//New Customer
			process.sourcing();
		}
		else {
			//Existing customer
			process.checkStage(applicationNumber);
		}
	}

}
}