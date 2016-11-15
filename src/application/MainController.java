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
	
	//�ʱ�ȭ�� �� ȣ��Ǵ� �޼ҵ�
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		Calendar calendar = Calendar.getInstance();
		int year = calendar.get(calendar.YEAR);
		int month = calendar.get(calendar.MONTH);
		dao = new HouseholdDAO();
		
		this.initCalendar(year, month);
	}
	
	//�޷��� �ٽ� �׷��ֱ� ���� �޼ҵ�
	public void initCalendar(int year, int month){
		//������ �׸��� �гο� �־��� ��� ��¥���� ����������
		gridPane_calendar.getChildren().removeAll(gridPane_calendar.getChildren());

		//�޷¿��� ���°���� ����� ���� ����
		int iDay = 0;
		
		//�޷� ��翡 �°� ��¥�� �������� �� �� 1���� ������������ ���������� 30������ 31������ ���س��ƾ� �Ѵ�
		Calendar calendar = Calendar.getInstance();
		
		//�޷��� �ش� ���� 1�Ϸ� �����Ѵ�.. �̶� ���� ���� ������ 1 ���� �Է��ؾ� ���ϴ� ���� ���´�.
		//��,��,��,��,��,��
		calendar.set(year,month,1,0,0,0);
		
		label_year.setText(calendar.get(calendar.YEAR)+"");
		label_month.setText(calendar.get(calendar.MONTH)+1+"");
		
		//1���� ������������ ����.. ������ �Ͽ����� 1�Ϻ��� �����Ͽ� ������� 7���� �ȴ� �迭�� ó���ϱ� ������ -1�Ͽ� �ʱⰪ�� 0�� �ǵ��� ������
		int firstDay = calendar.get(calendar.DAY_OF_WEEK)-1;
		
		//���� �ִ� �ϼ� (30���ΰ� 31���ΰ�)
		int lastDay = calendar.getActualMaximum(calendar.DAY_OF_MONTH);
		
		month++;
		String date = "";
		if(month<10){
			date = year + "0" + month + "01";
		}else{
			date = year + "" + month + "01";
		}
		
		//�޷¿� �Աݾ�,��ݾ�,�ܾ��� ǥ���ϱ� ���� DB�κ��� select �ؿ� ������
		List<HouseholdVO> householdList = dao.selectHouseholdList(date);
		
		//��¥�� ã�� ���� �ʿ� �Ű� ����
		HashMap<String,HouseholdVO> householdMap = new HashMap<>();
		for(HouseholdVO vo:householdList){
			householdMap.put(vo.getHouseholdDate(), vo);
		}
		
		try {
			//�׸��� �г��� �ο�(����) ������ŭ �ݺ�
			for(int inx=0;inx<6;inx++){
				//�׸��� �г��� �÷�(����) ������ŭ �ݺ�
				for(int jnx=0;jnx<7;jnx++){
					
					
					//�޷¿��� ��¥�� ���۵Ǵ� ù��° ���Ϻ��� ũ�� ������ ���ں��ٴ� ���� ������ ��¥ �г��� �ٿ��ִ´�
					if(iDay>=firstDay && iDay<firstDay+lastDay){
						
						//Day.fxml�� ����� ���� ��¥ ����� �ҷ��´�
						FlowPane day = FXMLLoader.load(getClass().getResource("Day.fxml"));
						
						//�ҷ��� ��¥ ��⿡�� ��ư�� ���̵�� ã�Ƴ��� ǥ�õǴ� ���� �ش糯¥�� �ٲ��ش�
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
							label_inamt.setText("�Ա�:"+householdMap.get(date2).getHouseholdInamt());
							label_outamt.setText("���:"+householdMap.get(date2).getHouseholdOutamt());
							label_total.setText("�ܾ�:"+householdMap.get(date2).getHouseholdTotal());
						}
						
						//�����ϱ� ���� �׸��� �гο� �ٿ��־��ش�
						gridPane_calendar.add(day, jnx, inx);
					}
					
					//��¥ ���� ���� ���� for���� �������� 1�� ����
					iDay++;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//�������� ������Ű�� �̺�Ʈ
	@FXML
	public void increaseYear(ActionEvent event){
		int year = Integer.parseInt(label_year.getText())+1;
		int month = Integer.parseInt(label_month.getText())-1;
		this.initCalendar(year, month);
	}
	
	//�������� ���ҽ�Ű�� �̺�Ʈ
	@FXML
	public void decreaseYear(ActionEvent event){
		int year = Integer.parseInt(label_year.getText())-1;
		int month = Integer.parseInt(label_month.getText())-1;
		this.initCalendar(year, month);
	}
	
	//���� ������Ű�� �̺�Ʈ
	@FXML
	public void increaseMonth(ActionEvent event){
		int year = Integer.parseInt(label_year.getText());
		int month = Integer.parseInt(label_month.getText());
		this.initCalendar(year, month);
	}
	
	//���� ���ҽ�Ű�� �̺�Ʈ
	@FXML
	public void decreaseMonth(ActionEvent event){
		int year = Integer.parseInt(label_year.getText());
		int month = Integer.parseInt(label_month.getText())-2;
		this.initCalendar(year, month);
	}
}
