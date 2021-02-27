package pq.jdev.b001.bookstore.charts.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pq.jdev.b001.bookstore.cart.model.Order;
import pq.jdev.b001.bookstore.cart.service.CartService;
import pq.jdev.b001.bookstore.charts.model.ChartsResponse;
import pq.jdev.b001.bookstore.charts.service.ChartsService;


@RestController
public class TestJson {
	
	@Autowired
	ChartsService chartService;

	@Autowired
	CartService cartService;
	
	@RequestMapping("/viewCharts")
	public List<ChartsResponse> testResonse(){
		List<Order> listOrders = cartService.findAll();
		List<ChartsResponse> chartsResponse = new ArrayList<>();
		chartsResponse.add(chartService.getDummyData1(listOrders));
		chartsResponse.add(chartService.getDummyData2(listOrders));
		return chartsResponse;
	}
}
