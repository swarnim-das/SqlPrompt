import java.sql.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.text.*;

public class SqlPrompt extends JFrame implements ActionListener {

	JFrame jf;
	JLabel l1;
	JTextField tf1,tf2;
	JButton b1,b2,b3;
	JTextArea ta;
    Container c;
    String query;
    Connection cn = null;

    /* ------------------Class to retrieve the Connection object ------------------------------*/
    public Connection Con() throws ClassNotFoundException,SQLException{

        Class.forName("oracle.jdbc.driver.OracleDriver");
        cn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:XE","system","system");
        return cn;

    }
    /*----------------------------------Constructor-------------------------------------------- */
    public SqlPrompt() {

    	jf = new JFrame("SQL Prompt");
    	c = jf.getContentPane();
    	c.setLayout(null); //FREE-HAND

    	ta = new JTextArea();
    	l1 = new JLabel("Enter Table Name");
    	tf1 = new JTextField();
    	tf2 = new JTextField();
    	b1 = new JButton("View Table");
        b2 = new JButton("Execute");
        b3 = new JButton("Clear");

        b1.addActionListener(this);
        b2.addActionListener(this);
        b3.addActionListener(this);

        /*----------------Set Positions---------------------------*/
        tf1.setBounds(10,19,400,23);
        tf2.setBounds(540,145,140,22);
        ta.setBounds(10,60,480,300);
        l1.setBounds(540,120,150,22);
        b1.setBounds(210,380,130,23);
        b2.setBounds(412,19,80,22);
        b3.setBounds(210,410,130,23);

        ta.setMargin(new Insets(2,8,2,4));
        ta.setEditable(false);

        /*----------------Add to Container------------------------*/
        c.add(tf1);
        c.add(l1);
        c.add(ta);
        c.add(b1);
        c.add(b2);
        c.add(tf2);
        c.add(b3);

        jf.getContentPane().setBackground(new Color(107, 106, 104));
        jf.setDefaultCloseOperation(EXIT_ON_CLOSE);
        jf.setSize(723,493);
        jf.setResizable(false);
        jf.setVisible(true);
        jf.setLocationRelativeTo(null);

        /*---------------------------------------------------------*/
    }

    @Override /*----------------ActionListener Method ---> actionPerformed--------------*/
    public void actionPerformed(ActionEvent actionevent) {

    	/*-----------------------Button VIEW TABLE--------------------------*/
    	if(actionevent.getSource() == b1) {

    		String table_name = tf2.getText();
    		if(table_name.equals("")) {
    			JOptionPane.showMessageDialog(null,"Please Enter TABLE NAME");
    			return;
    		}
    		String query = "select * from "+table_name;
    		ta.setText("");
    		try {
    			cn = Con(); /*---------Connection Object---------------*/
    			Statement st = cn.createStatement();
        		ResultSet rs = st.executeQuery(query);
        		ResultSetMetaData data = rs.getMetaData();
        		int cols = data.getColumnCount();
        		String data_type[] = new String[cols+1];
        		String col_name[] = new String[cols+1];

        		/*-------------Retrieval of ResultSet Properties(ResultSetMetaData)--------*/
        		for(int i = 1;i <= cols; i++) {

        			col_name[i] = data.getColumnLabel(i);
        			ta.append(data.getColumnLabel(i)+"\t    ");
        			data_type[i] = data.getColumnTypeName(i);
        		}

        		ta.append("\n\n------------------------------------------------");
        		ta.append("-------------------------------------\n");

        		/*--------------------------Display Of Data--------------------------------*/
        		while(rs.next()) {
        			String result = "";
        			int k = 1;

        			while(k <= cols) {

        				/*---------Switch Case to retrieve Datatype-----------------*/
        				switch(data_type[k]) {

        					/*----------NUMBER Datatype--------*/
        					case "NUMBER" :

        							int num = 0;
        							double d = rs.getDouble(k);
        							if(d%1 == 0.0) {
        								num = (int)d;
        								ta.append(Integer.toString(num)+"\t   ");
        								k++;
        							}
        							else {
        								ta.append(Double.toString(d)+"\t   ");
        								k++;
        							}
        							break;

        					/*-----------VARCHAR Datatype----------*/
        					case "VARCHAR2" :

        							ta.append(rs.getString(k)+"\t   ");
        							k++;

        							break;

        					/*-----------DATE Datatype-------------*/
        					case "DATE" :

        							java.sql.Date s_date = rs.getDate(k);
        							java.util.Date j_date = (java.util.Date)s_date;

        							SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy"); /*--ORACLE DATE MM Capital--*/
        							ta.append(sdf.format(j_date)+"\t   ");
        							k++;
        							break;
        				}
        			}
        			ta.append("\n");
        		}
    		}
    		catch (ClassNotFoundException ce) {
				ta.setText("");
				ta.append("Connection Error\nPlease check server logs for more details.");
    			ce.printStackTrace();
    		}
    		catch (SQLException se) {
    			ta.setText("");
				ta.append("SQL Error\nPlease check server logs for more details.");
    			se.printStackTrace();
    		}
    	}

    	/*-----------------------Button EXECUTE--------------------------*/
    	if(actionevent.getSource() == b2) {

    		ta.setText("");
    		String query = tf1.getText();
    		if(query.equals("")) {
    			JOptionPane.showMessageDialog(null,"Please enter a Query");
    			return;
    		}
    		if((query.charAt(query.length()-1)) == ';')      /*---------Omitting the semicolon------------*/
    			query = query.substring(0,query.length()-1);

    		try {

    			cn = Con(); /*---------Connection Object---------------*/
    			Statement st = cn.createStatement();
    			boolean b = st.execute(query);

    			if(b) {     /*-----------------Select Statements--------------*/
    				ResultSet rs = st.getResultSet();
    				ResultSetMetaData data = rs.getMetaData();
        			int cols = data.getColumnCount();
        			String data_type[] = new String[cols+1];
        			String col_name[] = new String[cols+1];

        			/*-------------Retrieval of ResultSet Properties(ResultSetMetaData)--------*/
        			for(int i = 1;i <= cols; i++) {

	        			col_name[i] = data.getColumnLabel(i);
	        			ta.append(data.getColumnLabel(i)+"\t    ");
	        			data_type[i] = data.getColumnTypeName(i);
	        		}

	        		ta.append("\n\n------------------------------------------------");
	        		ta.append("-------------------------------------\n");

        			/*--------------------------Display Of Data--------------------------------*/
        			while(rs.next()) {
        				String result = "";
        				int k = 1;

        				while(k <= cols) {

        					/*---------Switch Case to retrieve Datatype-----------------*/
        					switch(data_type[k]) {

        						/*----------NUMBER Datatype--------*/
        						case "NUMBER" :

        							int num = 0;
        							double d = rs.getDouble(k);
        							if(d%1 == 0.0) {
        								num = (int)d;
        								ta.append(Integer.toString(num)+"\t   ");
        								k++;
        							}
        							else {
        								ta.append(Double.toString(d)+"\t   ");
        								k++;
        							}
        							break;

        						/*-----------VARCHAR Datatype----------*/
        						case "VARCHAR2" :

        								ta.append(rs.getString(k)+"\t   ");
        								k++;

        								break;

        						/*-----------DATE Datatype-------------*/
        						case "DATE" :

        								java.sql.Date s_date = rs.getDate(k);
        								java.util.Date j_date = (java.util.Date)s_date;

        								SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy"); /*--ORACLE DATE MM Capital--*/
        								ta.append(sdf.format(j_date)+"\t   ");
        								k++;
        								break;
        					}
	        			}
	        			ta.append("\n");
	        		}

	    		}
	    		else {
                    int k = st.getUpdateCount();
                    ta.append(k + " times updated");
                }
	    	}
	    	catch (ClassNotFoundException ce) {
				ta.setText("");
				ta.append("Connection Error\nPlease check server logs for more details.");
    			ce.printStackTrace();
    		}
    		catch (SQLException se) {
    			ta.setText("");
				ta.append("SQL Error\nPlease check server logs for more details.");
    			se.printStackTrace();
    		}
    	}

    	/*-----------------------Button CLEAR-----------------------------*/
    	if(actionevent.getSource() == b3) {
    		ta.setText("");
    		tf1.setText("");
    		tf2.setText("");
    	}
    }

    public static void main(String[] args) {
    	SqlPrompt sqlprompt = new SqlPrompt();
    }
}
