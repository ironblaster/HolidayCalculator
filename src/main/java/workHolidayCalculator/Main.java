package workHolidayCalculator;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

public class Main {
	
	
	public static void main(String[] args) {
		String countryCode="IT";
		int year= 2022;
		
		boolean satIsFree = true;
		boolean sunIsFree = true;
		int dayOfPersonalHoliday = 5;
		int specialPermission = 0;
		
		
		List <LocalDate> cal = getPersonalHolidays(getHolidays(countryCode, year),satIsFree,sunIsFree,year);

		

		
		System.out.println(cal.size());
		
		
		CalcoloMagico(cal, dayOfPersonalHoliday, specialPermission,year);

	}
	
	
	
	private static void CalcoloMagico(List<LocalDate> giorniFree,int giornipersonali,int giorniSpeciali,int year) {
		
		int giorniEffettivi=0;
		LocalDate giornoPartenza = null;
		
		
		LocalDate StartDate = LocalDate.of(year, 1, 1);
		LocalDate EndDate = LocalDate.of(year, 12, 31);
		
		LocalDate StartHoly;
		LocalDate endHoly;
		
		while(StartDate.isBefore(EndDate)) {
			int possibiliGiorni=0;
			if(!giorniFree.contains(StartDate)) {
				LocalDate giornoComodo = StartDate;
				StartHoly=StartDate;
				possibiliGiorni=0;
				possibiliGiorni=possibiliGiorni+giornipersonali;
				
				LocalDate giornoComodoPositivo=giornoComodo.plusDays(5);
				while(giorniFree.contains(giornoComodoPositivo.plusDays(1))){
					possibiliGiorni++;
					giornoComodoPositivo=giornoComodoPositivo.plusDays(1);
				}
				while (giorniFree.contains(giornoComodo.minusDays(1))) {
					possibiliGiorni++;
					giornoComodo=giornoComodo.minusDays(1);
				}
				if(giorniEffettivi<possibiliGiorni) {
					giorniEffettivi=possibiliGiorni;
					giornoPartenza=StartHoly;
					
					if(giorniSpeciali>=1) {
						boolean positive = true;
						int possibiliSpeciali = possibiliGiorni+giorniSpeciali;
						giornoComodo=giornoPartenza.minusDays(giorniSpeciali);
						giornoComodoPositivo=giornoPartenza.plusDays(giornipersonali);
						
						
						while(giorniFree.contains(giornoComodoPositivo.plusDays(1))){
							possibiliSpeciali++;
							giornoComodoPositivo=giornoComodoPositivo.plusDays(1);
						}
						
						if(possibiliSpeciali-possibiliGiorni<1) {
							positive = false;
						while (giorniFree.contains(giornoComodo.minusDays(1))) {
							possibiliSpeciali++;
							giornoComodo=giornoComodo.minusDays(1);
							}
						}
					
						if(possibiliSpeciali-possibiliGiorni>1) {
							if(positive) 
								StartHoly.plusDays(possibiliSpeciali-possibiliGiorni);
							else
								StartHoly.minusDays(possibiliSpeciali-possibiliGiorni);
							
							
							giorniEffettivi=possibiliSpeciali;
							giornoPartenza=StartHoly;
						}
					}
					
					
					
					
					
				}
			}
			
			
			StartDate=StartDate.plusDays(1);
		}
		
		
		System.out.println("giorni festivi: "+giorniEffettivi);
		System.out.println("anno:"+giornoPartenza.getYear()+" mese:"+giornoPartenza.getMonth()+" giorno:"+giornoPartenza.getDayOfMonth());
		
		
	}
	
	

	
	private static List<LocalDate> getPersonalHolidays(List<Date> stateHoli,boolean sat,boolean sun,int year){

		List<LocalDate> calendars = new ArrayList<LocalDate>();
		

		
		LocalDate StartDate = LocalDate.of(year, 1, 1);
		LocalDate EndDate = LocalDate.of(year, 12, 31);
		
		
		
		while(StartDate.isBefore(EndDate)) {
			
			
			
			
			if(StartDate.getDayOfWeek().equals(DayOfWeek.SATURDAY)&&sat) {
				calendars.add(StartDate);
			}
			
			if(StartDate.getDayOfWeek().equals(DayOfWeek.SUNDAY)&&sun) {
				calendars.add(StartDate);
			}
			
			
			StartDate=StartDate.plusDays(1);
		}
		
		
		
		
		for (Date d : stateHoli) {
			if(!calendars.contains(d.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()))
			calendars.add(d.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
			
		}
		
		
		
		
		
		
		return calendars;
		
	}
	
	
	
	
	
	
	
	private static List<Date> getHolidays(String countryCode,int year){
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		
		List<Date> days = new ArrayList<Date>();
		
		try {
			HttpResponse<String> response = Unirest.get("https://public-holiday.p.rapidapi.com/"+year+"/"+countryCode)
					.header("x-rapidapi-host", "public-holiday.p.rapidapi.com")
					                    
					.header("x-rapidapi-key", "API-KEY")
					.asString();
			
		String [] data = response.getBody().split(":");
	
			for (String s : data) {
				
				String [] val = s.split(",");
				
				
				
				for (String valore : val) {
				if (valore.contains(""+year)) {
					
					try {
						days.add(format.parse(valore.replaceAll("\"", "")));
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				}
			}
			
			
		} catch (UnirestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return days;
		
	}
	
	

}
