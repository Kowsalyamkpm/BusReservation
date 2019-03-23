package bus.reservation.startup;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Scanner;

public class BusReservation
{
	private static HashMap<String, Bus> busMap= new HashMap<>();
	public static void main(String args[])
	{
		try
		{
			System.out.println("*****Welcome to Bus reservation");
			loadBusDetails();
			printMainMenu();
			Scanner sc = new Scanner(System.in);
			boolean flag = false;
			while(!flag)
			{
				String temp = sc.nextLine().trim();
				switch (temp) {
				case "1":
					reserveBus(sc);
					flag = true;
					break;
				case "2":
					flag = true;
					System.out.println("Exiting");
					break;
				default:
					System.out.println("Please provide the correct value");
					printMainMenu();
					break;
				}
			}
			sc.close();
		}
		catch (Exception e) {
			System.err.println("Exception occured due to "+ e);
		}
	}
	
	private static void reserveBus(Scanner sc)
	{
		try
		{
			boolean flag1 = false;
			while(!flag1)
			{
				listBusMenu();
				String busInput = sc.nextLine().trim();
				if(BusReservation.busMap.containsKey(busInput))
				{
					boolean travelDateFlag = false;
					while(!travelDateFlag)
					{
						System.out.println("Enter a date of travel (dd/mm/yyyy)");
						String traveldateStr = sc.nextLine().trim();
						Date currentDate = new Date();
						Date travelDate = null;
						try
						{
							travelDate = new SimpleDateFormat("dd/MM/yyyy").parse(traveldateStr);
						}
						catch(Exception e)
						{
							System.out.println("Please enter a correct date in the dd/MM/yyyy format");
							continue;
						}
						if(travelDate.before(currentDate))
						{
							System.out.println("Please provide a different date");
						}
						else
						{
							travelDateFlag = true;
							Bus selectedBus = BusReservation.busMap.get(busInput);
							Integer availableSeats = getAvailableSeats(selectedBus, travelDate);
							if(availableSeats != null && availableSeats == 0)
							{
								System.out.println("Seat is not available for the current selection. Try different");
							}
							else
							{
								System.out.println("Selected bus name is "+ selectedBus.getBusName());
								System.out.println("Total seats are ..."+ selectedBus.getCapacity());
								System.out.println("Available seats are ...."+ availableSeats);
								boolean seatsFlag = false;
								while(!seatsFlag)
								{
									System.out.println("Enter a required no of seats");
									String seatCountStr = sc.nextLine().trim();
									try
									{
										int seatCount = Integer.parseInt(seatCountStr);
										if(seatCount > availableSeats)
										{
											System.out.println("Please give a seats count within an available limit");
										}
										else
										{
											seatsFlag = true;
											flag1 = true;
											System.out.println("Enter your name");
											String name = sc.nextLine().trim();
											bookSeats(selectedBus, travelDate,name, seatCount);
											System.exit(0);
										}
									}
									catch (Exception e)
									{
										System.out.println("Please give numeric value");
									}
								}
							}
						}
					}
				}
				else
				{
					System.out.println("No bus is available for the given name");
				}
			}
			
		}
		catch(Exception e)
		{
			System.out.println("Exception occured ..."+e);
		}
	}

	private static void bookSeats(Bus selectedBus, Date travelDate, String name, int seatCount)
	{
		try
		{
			File busDateFile = new File("data"+ File.separator +selectedBus.getBusName() + "_"+ travelDate + ".txt");
			if(!busDateFile.exists())
			{
				busDateFile.createNewFile();
			}
			
			BufferedWriter writer = new BufferedWriter(new FileWriter(busDateFile, true));
			writer.write(name+","+seatCount);
			writer.newLine();
			writer.close();
			System.out.println("Your ticket is booked successfully. Thank You!!!");
		}
		catch (Exception e) {
			System.err.println("Exception due to "+ e);
		}
	}

	private static Integer getAvailableSeats(Bus selectedBus, Date travelDate)
	{
		File busDateDir = new File("/home/kowsalya/eclipse-workspace/Bus_reservation/busDir");
		if(!busDateDir.exists())
		{
			busDateDir.mkdirs();
		}
		File busDateFile = new File("/home/kowsalya/eclipse-workspace/Bus_reservation/busDir/"+selectedBus.getBusName() + "_"+ travelDate + ".txt");
		if(!busDateFile.exists())
		{
			return selectedBus.getCapacity();
		}
		else
		{
			try
			{
				BufferedReader reader = new BufferedReader(new FileReader(busDateFile));
				String temp = null;
				int bookedSeatsCount = 0;
				while((temp = reader.readLine()) != null)
				{
					String[] tempStr = temp.split(",");
					bookedSeatsCount += Integer.parseInt(tempStr[1]);
				}
				reader.close();
				return selectedBus.getCapacity() - bookedSeatsCount;
			}
			catch (Exception e)
			{
				System.err.println("Exception occured ..."+e);
			}
		}
		return null;
	}

	public static void printMainMenu()
	{
		System.out.println("Choose any of the below options");
		System.out.println("1. Check availability and book");
		System.out.println("2. Exit");
	}
	
	public static void loadBusDetails()
	{
		try
		{
			if(BusReservation.busMap.isEmpty())
			{
				File busDetailsFile = new File("data" +File.separator+"buses.txt");
				BufferedReader reader = new BufferedReader(new FileReader(busDetailsFile));
				String line = null;
				while((line = reader.readLine()) != null)
				{
					String[] t1 = line.split(",");
					Bus bus = new Bus();
					bus.setBusName(t1[0]);
					bus.setFrom(t1[1]);
					bus.setTo(t1[2]);
					bus.setStartTime(t1[3]);
					bus.setEndTime(t1[4]);
					bus.setCapacity(Integer.parseInt(t1[5]));
					BusReservation.busMap.put(bus.getBusName(), bus);
				}
				reader.close();
			}
		}
		catch (Exception e) {
			System.err.println("exception occured due to ..."+ e);
		}
	}
	
	public static void listBusMenu()
	{
		System.out.println("Bus list are");
		for(Bus bus: BusReservation.busMap.values())
		{
			System.out.println("Bus Name: " + bus.getBusName());
			System.out.println("From : "+ bus.getFrom());
			System.out.println("To : "+ bus.getTo());
			System.out.println("Start time : "+ bus.getStartTime());
			System.out.println("End time: "+bus.getEndTime());
			System.out.println("Capacity: "+ bus.getCapacity());
		}
		System.out.println("Enter a bus Name");
	}
}
