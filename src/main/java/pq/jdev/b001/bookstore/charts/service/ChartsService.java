package pq.jdev.b001.bookstore.charts.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;

import pq.jdev.b001.bookstore.cart.model.Order;
import pq.jdev.b001.bookstore.charts.model.ChartsResponse;
import pq.jdev.b001.bookstore.charts.model.Dataset;



@Service
public class ChartsService {

	public ChartsResponse getDummyData1(List<Order> listOrders){
		ChartsResponse chartsResponse = new ChartsResponse();
		chartsResponse.setAppName("TOTAL BILL");
		
		List<String> lables = new ArrayList<>();
		lables.add("January");
		lables.add("February");
		lables.add("March");
		lables.add("April");
		lables.add("May");
		lables.add("June");
		lables.add("July");
		lables.add("August");
		lables.add("September");
		lables.add("October");
		lables.add("November");
		lables.add("December");
		chartsResponse.setLables(lables);
		
		List<Dataset> datasets = new ArrayList<>();
		Dataset dataset = new Dataset();
		List<Integer> value = new ArrayList<>();
		int[] arrayIntMonth = {0, 0 ,0, 0, 0, 0, 0, 0, 0, 0, 0, 0};	
		for(Order order : listOrders) {
			Integer month = getMonthInt(order.getOrderDate());
			
			for(Integer i = 0; i < 12; i++) {
				int montTemp = i;
				if(month == montTemp + 1) {
					arrayIntMonth[i] = arrayIntMonth[i] + 1;
				} else if(month == null) {
					arrayIntMonth[i] = 0;
				}
			}
		}

		for(Integer i = 0; i < 12; i++) {
			value.add(arrayIntMonth[i]);
		}
				
		dataset.setName("Total Bill Success");				
		dataset.setValue(value);
		datasets.add(dataset);
		
		// dataset = new Dataset();
		// value = new ArrayList<>();
		// for(Integer i = 0; i < 12; i++) {
		// 	value.add(arrayIntMonth[i]);
		// }
		// dataset.setName("Total Bill Fail");		
		// dataset.setValue(value);
		// datasets.add(dataset);
		
		chartsResponse.setDatasets(datasets);
		
		return chartsResponse;
	}

	public ChartsResponse getDummyData2(List<Order> listOrders){
		ChartsResponse chartsResponse = new ChartsResponse();
		chartsResponse.setAppName("TOTAL PRICE");
		
		List<String> lables = new ArrayList<>();
		lables.add("January");
		lables.add("February");
		lables.add("March");
		lables.add("April");
		lables.add("May");
		lables.add("June");
		lables.add("July");
		lables.add("August");
		lables.add("September");
		lables.add("October");
		lables.add("November");
		lables.add("December");

		chartsResponse.setLables(lables);
		
		List<Dataset> datasets = new ArrayList<>();
		Dataset dataset = new Dataset();
		List<Integer> value = new ArrayList<>();		
		int[] arrayIntMonth = {0, 0 ,0, 0, 0, 0, 0, 0, 0, 0, 0, 0};	
		for(Order order : listOrders) {
			Integer month = getMonthInt(order.getOrderDate());
			Integer year = getYearInt(order.getOrderDate());
			Double price = order.getAmount();
			System.out.println("year: " + year);
			for(Integer i = 0; i < 12; i++) {
				int montTemp = i;
				if(month == montTemp + 1) {
					arrayIntMonth[i] += price;
				} else if(month == null) {
					arrayIntMonth[i] = 0;
				}
			}
		}

		for(Integer i = 0; i < 12; i++) {
			value.add(arrayIntMonth[i]);
		}
		dataset.setName("Total Price Interest");				
		dataset.setValue(value);
		datasets.add(dataset);
		
		// dataset = new Dataset();
		// value = new ArrayList<>();
		// value.add(28);
		// value.add(70);
		// value.add(40);
		// value.add(19);
		// value.add(20);
		// value.add(27);
		// value.add(90);
		// dataset.setName("Total Price Losses");		
		// dataset.setValue(value);
		// datasets.add(dataset);
		
		chartsResponse.setDatasets(datasets);
		
		return chartsResponse;
	}

	public static int getMonthInt(Date date) {

		SimpleDateFormat dateFormat = new SimpleDateFormat("MM");
		return Integer.parseInt(dateFormat.format(date));
	}

	public static int getYearInt(Date date) {

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy");
		return Integer.parseInt(dateFormat.format(date));
	}

}
