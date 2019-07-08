package com.brainmentors.los.operation;

import com.brainmentors.los.customer.Customer;
import com.brainmentors.los.customer.LoanDetails;
import com.brainmentors.los.customer.PersonalInformation;
import com.brainmentors.los.utils.CommonConstants;
import com.brainmentors.los.utils.LoanConstants;
import com.brainmentors.los.utils.StageConstants;
import com.brainmentors.los.utils.Utility;

import static com.brainmentors.los.utils.Utility.scanner;
import static com.brainmentors.los.utils.Utility.serialCounter;

import java.util.ArrayList;

public class LOSProcess implements StageConstants,CommonConstants{
	//private Customer customers[]=new Customer[100];
	private ArrayList<Customer>customers=new ArrayList<>(); //generic 
	
	public void approval(Customer customer) {
		customer.setStage(APPROVAL);
		int score=customer.getLoanDetails().getScore();
		System.out.println(" id "+customer.getId());
		System.out.println("Name is "+customer.getPersonal().getFirstName()
				+" "+customer.getPersonal().getLastName());
		System.out.println("Score is "+customer.getLoanDetails().getScore());
		System.out.println("Loan "+customer.getLoanDetails().getType()
				+" Amount "+customer.getLoanDetails().getAmount()
				+"Duration "+customer.getLoanDetails().getDuration());
		double approveAmount=customer.getLoanDetails().getAmount()*(score/100);
		System.out.println("Loan Approved Amount is "+approveAmount);
		System.out.println("Do you want to bring this loan or not");
		char choice=scanner.next().toUpperCase().charAt(0);
		if(choice==NO) {
			customer.setStage(REJECT);
			customer.setRemarks("Customer deny the approved amount"+approveAmount);
			return;
		}
		else {
			showEMI(customer);
		}
	}
	private void showEMI(Customer customer) {
		System.out.println("EMI is ");
		if(customer.getLoanDetails().getType().equalsIgnoreCase(LoanConstants.HOME_LOAN)) {
			customer.getLoanDetails().setRoi(LoanConstants.HOME_LOAN_ROI);
		}
		if(customer.getLoanDetails().getType().equalsIgnoreCase(LoanConstants.AUTO_LOAN)) {
			customer.getLoanDetails().setRoi(LoanConstants.AUTO_LOAN_ROI);
		}
		if(customer.getLoanDetails().getType().equalsIgnoreCase(LoanConstants.PERSONAL_LOAN)) {
			customer.getLoanDetails().setRoi(LoanConstants.PERSONAL_LOAN_ROI);
		}
		double perMonthprinciple = customer.getLoanDetails().getAmount()/customer.getLoanDetails().getDuration();
		double interest =perMonthprinciple*customer.getLoanDetails().getRoi();
		double totalEmi= perMonthprinciple+interest;
		System.out.println("Your EMI is"+totalEmi);
	}
	
	public void qde(Customer customer) {
		customer.setStage(QDE);
		System.out.println("Application Number : "+customer.getId());
		System.out.println("Name : "+customer.getPersonal().getFirstName()+" "+
		customer.getPersonal().getLastName());
		System.out.println("You Applied for a : "+customer.getLoanDetails().getType()+
				"\t Duration : "+customer.getLoanDetails().getDuration()+"\t Amount : "+
				customer.getLoanDetails().getAmount());
		System.out.println("Enter the PanCard Number : ");
		String panCard=scanner.next();
		System.out.println("Enter the VoterId");
		String voterId=scanner.next();
		System.out.println("Enter the Income");
		double income=scanner.nextDouble();
		System.out.println("Enter the Liability");
		double liability=scanner.nextDouble();
		System.out.println("Enter the phone");
		String phone=scanner.next();
		System.out.println("Enter the Email");
		String email=scanner.next();
		customer.getPersonal().setVoterId(voterId);
		customer.getPersonal().setPanCard(panCard);
		customer.getPersonal().setPhone(phone);
		customer.getPersonal().setEmail(email);
		customer.setIncome(income);
		customer.setLiability(liability);
	}

	public void moveToNextStage(Customer customer) {
		while(true) {
		if(customer.getStage()==SOURCING) {
			System.out.println("want to move to the next stage Y/N");
			char choice=scanner.next().toUpperCase().charAt(0);
			if(choice==YES) {
				qde(customer);
			}
			else {
				return;
			}
		
		}
		else
		if(customer.getStage()==QDE) {
			System.out.println("QDE "
					+ "want to move to the next stage Y/N");
			char choice=scanner.next().toUpperCase().charAt(0);
			if(choice==YES) {
				dedupe(customer);
			}
			else {
				return;
			}
		  }
		else
		if(customer.getStage()==DEDUPE) {
			System.out.println(" Dedupe want to move to the next stage Y/N");
			char choice=scanner.next().toUpperCase().charAt(0);
			if(choice==YES) {
				scoring(customer);
			}
			else {
				return;
			}
		  }
		
		if(customer.getStage()==SCORING) {
			System.out.println(" Scoring want to move to the next stage Y/N");
			char choice=scanner.next().toUpperCase().charAt(0);
			if(choice==YES) {
				approval(customer);
			}
			else {
				return;
			}
		  }
		}
	}
	
	public void scoring(Customer customer) {
		customer.setStage(SCORING);
		int score=0;
		double totalIncome=customer.getIncome()-customer.getLiability();
		if(customer.getPersonal().getAge()>=21 &&
				customer.getPersonal().getAge()<=35) {
			score+=50;
		}
		if(totalIncome>=200000) {
			score+=50;
		}
		customer.getLoanDetails().setScore(score);
		}
				

	
	public void dedupe(Customer customer) {
		//System.out.println("inside dedupe");
		customer.setStage(DEDUPE);
		boolean isNegativeFound=false;
		for(Customer negativeCustomer:DB.getNegativeCustomers()) {
			int negativeScore=isNegative(customer,negativeCustomer);
			if(negativeScore>0) {
				System.out.println("Customer Record Found in Dedupe and score is "+negativeScore);
				isNegativeFound=true;
				break;
			}
			}
			if(isNegativeFound) {
				System.out.println("Do You Want To proceed this Loan"+customer.getId());
				char choice=scanner.next().toUpperCase().charAt(0);
				if(choice==NO) {
					customer.setRemarks("Loan is rejected,due to high score in Dedupe check");
					customer.setStage(REJECT);
					return;
				}
			}
		}
		
	
	private int isNegative(Customer customer,Customer negative) {
		int percentageMatch=0;
		if(customer.getPersonal().getPhone().equals(negative.getPersonal().getPhone())) {
			percentageMatch+=20;
		}
		
		if(customer.getPersonal().getEmail().equals(negative.getPersonal().getEmail())) {
			percentageMatch+=20;
		}
		if(customer.getPersonal().getVoterId().equals(negative.getPersonal().getVoterId())) {
			percentageMatch+=20;
		}
		if(customer.getPersonal().getPanCard().equals(negative.getPersonal().getPanCard())) {
			percentageMatch+=20;
		}
	    if(customer.getPersonal().getAge()==negative.getPersonal().getAge() && 
				customer.getPersonal().getFirstName()
				.equalsIgnoreCase(negative.getPersonal().getFirstName())) {
			percentageMatch+=20;
		}
		return percentageMatch;
	}
	
	public void sourcing() {
		Customer customer=new Customer();
		customer.setId(serialCounter);
		customer.setStage(SOURCING);
		
		System.out.println("Enter the Firstname");
		String firstName = scanner.next();
		System.out.println("Enter the LastName");
		String lastName=scanner.next();
		System.out.println("Enter the Age");
		int age=scanner.nextInt();
	    System.out.println("Enter the Loan Type HL,AL,PL");
		String type=scanner.next();
		System.out.println("Enter the amount");
		double amount=scanner.nextDouble();
		System.out.println("Duration of Loan");
		int duration=scanner.nextInt();
		PersonalInformation pd=new PersonalInformation();
		pd.setFirstName(firstName);
		pd.setLastName(lastName);
		pd.setAge(age);
		customer.setPersonal(pd);
		LoanDetails loanDetails=new LoanDetails();
		loanDetails.setType(type);
		loanDetails.setAmount(amount);
		loanDetails.setDuration(duration);
		customer.setLoanDetails(loanDetails);
		customers.add(customer);
		serialCounter++;
		System.out.println("Sourcing done>>>>");
		}
	
	public void checkStage(int applicationNumber) {
		boolean isStageFound=false;
		if(customers!=null && customers.size()>0) {
		for(Customer customer:customers) {
			if(customer.getId()==applicationNumber) {
				System.out.println("you are on"+
			Utility.getStageName(customer.getStage()));
				isStageFound=true;
				moveToNextStage(customer);
				break;
			}
		}
		}
		if(!isStageFound) {
			System.out.println("Invalid application number");
		}
		
	}

}
