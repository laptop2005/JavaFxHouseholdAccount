package application;

import java.io.IOException;
import java.net.URL;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

import dao.HouseholdDAO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import vo.HouseholdVO;

public class MainController implements Initializable{
	@FXML private GridPane gridPane_calendar;
	@FXML private Label label_year;
	@FXML private Label label_month;
	@FXML private Button button_increaseYear;
	@FXML private Button button_decreaseYear;
	@FXML private Button button_increaseMonth;
	@FXML private Button button_decreaseMonth;
	
	private HouseholdDAO dao;
	
	//초기화될 때 호출되는 메소드
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		Calendar calendar = Calendar.getInstance();
		int year = calendar.get(calendar.YEAR);
		int month = calendar.get(calendar.MONTH);
		dao = new HouseholdDAO();
		
		this.initCalendar(year, month);
	}
	
	//달력을 다시 그려주기 위한 메소드
	public void initCalendar(int year, int month){
		//기존에 그리드 패널에 있었던 모든 날짜들을 지워버린다
		gridPane_calendar.getChildren().removeAll(gridPane_calendar.getChildren());

		//달력에서 몇번째인지 세어보기 위한 변수
		int iDay = 0;
		
		//달력 모양에 맞게 날짜를 넣으려면 그 달 1일이 무슨요일인지 마지막날은 30일인지 31일인지 구해놓아야 한다
		Calendar calendar = Calendar.getInstance();
		
		//달력을 해당 달의 1일로 셋팅한다.. 이때 월은 실제 월보다 1 적게 입력해야 원하는 달이 나온다.
		//연,월,일,시,분,초
		calendar.set(year,month,1,0,0,0);
		
		label_year.setText(calendar.get(calendar.YEAR)+"");
		label_month.setText(calendar.get(calendar.MONTH)+1+"");
		
		//1일이 무슨요일인지 구함.. 요일은 일요일이 1일부터 시작하여 토요일이 7일이 된다 배열로 처리하기 때문에 -1하여 초기값이 0이 되도록 맞춰준
		int firstDay = calendar.get(calendar.DAY_OF_WEEK)-1;
		
		//월의 최대 일수 (30일인가 31일인가)
		int lastDay = calendar.getActualMaximum(calendar.DAY_OF_MONTH);
		
		month++;
		String date = "";
		if(month<10){
			date = year + "0" + month + "01";
		}else{
			date = year + "" + month + "01";
		}
		
		//달력에 입금액,출금액,잔액을 표시하기 위해 DB로부터 select 해온 데이터
		List<HouseholdVO> householdList = dao.selectHouseholdList(date);
		
		//날짜로 찾기 위해 맵에 옮겨 담자
		HashMap<String,HouseholdVO> householdMap = new HashMap<>();
		for(HouseholdVO vo:householdList){
			householdMap.put(vo.getHouseholdDate(), vo);
		}
		
		try {
			//그리드 패널의 로우(가로) 갯수만큼 반복
			for(int inx=0;inx<6;inx++){
				//그리드 패널의 컬럼(세로) 갯수만큼 반복
				for(int jnx=0;jnx<7;jnx++){
					
					
					//달력에서 날짜가 시작되는 첫번째 요일보다 크고 마지막 일자보다는 값이 작을때 날짜 패널을 붙여넣는다
					if(iDay>=firstDay && iDay<firstDay+lastDay){
						
						//Day.fxml로 만들어 놓은 날짜 모듈을 불러온다
						FlowPane day = FXMLLoader.load(getClass().getResource("Day.fxml"));
						
						//불러온 날짜 모듈에서 버튼의 아이디로 찾아내어 표시되는 값을 해당날짜로 바꿔준다
						Button button_date = (Button)day.lookup("#button_date");
						button_date.setText(iDay-firstDay+1+"");
						
						String date2 = "";
						if(month<10){
							date2 = year + "0" + month;
						}else{
							date2 = year + "" + month;
						}
						if(iDay-firstDay+1<10){
							date2 += "0" + (iDay-firstDay+1);
						}else{
							date2 += (iDay-firstDay+1);
						}
						
						if(householdMap.get(date2)!=null){
							Label label_inamt = (Label)day.lookup("#label_inamt");
							Label label_outamt = (Label)day.lookup("#label_outamt");
							Label label_total = (Label)day.lookup("#label_total");
							label_inamt.setText("입금:"+householdMap.get(date2).getHouseholdInamt());
							label_outamt.setText("출금:"+householdMap.get(date2).getHouseholdOutamt());
							label_total.setText("잔액:"+householdMap.get(date2).getHouseholdTotal());
						}
						
						//관리하기 위한 그리드 패널에 붙여넣어준다
						gridPane_calendar.add(day, jnx, inx);
					}
					
					//날짜 수를 세기 위해 for문이 돌때마다 1씩 증가
					iDay++;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//연도값을 증가시키는 이벤트
	@FXML
	public void increaseYear(ActionEvent event){
		int year = Integer.parseInt(label_year.getText())+1;
		int month = Integer.parseInt(label_month.getText())-1;
		this.initCalendar(year, month);
	}
	
	//연도값을 감소시키는 이벤트
	@FXML
	public void decreaseYear(ActionEvent event){
		int year = Integer.parseInt(label_year.getText())-1;
		int month = Integer.parseInt(label_month.getText())-1;
		this.initCalendar(year, month);
	}
	
	//월을 증가시키는 이벤트
	@FXML
	public void increaseMonth(ActionEvent event){
		int year = Integer.parseInt(label_year.getText());
		int month = Integer.parseInt(label_month.getText());
		this.initCalendar(year, month);
	}
	
	//월을 감소시키는 이벤트
	@FXML
	public void decreaseMonth(ActionEvent event){
		int year = Integer.parseInt(label_year.getText());
		int month = Integer.parseInt(label_month.getText())-2;
		this.initCalendar(year, month);
	}
}
