package com.topica.edu.itlab.jdbc.tutorial;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.topica.edu.itlab.jdbc.tutorial.entity.ClassEntity;
import com.topica.edu.itlab.jdbc.tutorial.entity.StudentEntity;


public class App {

	public static List<ClassEntity> listClass = new ArrayList<ClassEntity>();
	public static List<StudentEntity> listStudent;
	
	// lazy load
	public static void lazyLoad(Statement stmt) throws SQLException {
		ResultSet rs = stmt.executeQuery("select * from class");
		ResultSet rsStudent;
		long index;
		// read from database
		while (rs.next()) {
			ClassEntity classEntity = new ClassEntity();
			classEntity.setId(rs.getLong(1));
			classEntity.setName(rs.getString(2));
			// add classEntity to list class
			listClass.add(classEntity);
		}
		
		for (int i = 0; i < listClass.size(); i++) {
			listStudent = new ArrayList<StudentEntity>();
			// if listStudent = null
			if (listClass.get(i).getListStudent() == null) {
				index = listClass.get(i).getId();
				// read from table student
				rsStudent = stmt.executeQuery("SELECT * FROM `student` WHERE `class_id` = '" + index + "'");
				while (rsStudent.next()) {
					StudentEntity student = new StudentEntity();
					student.setId(rsStudent.getLong(1));
					student.setName(rsStudent.getString(2));
					listStudent.add(student);
				}
			}
			// then set it to lissClass
			listClass.get(i).setListStudent(listStudent);
		}
		// write to check
		for (int i = 0; i < listClass.size(); i++) {
			System.out.println("Student of class " + listClass.get(i).getName());
			for (int j = 0; j < listClass.get(i).getListStudent().size(); j++)
				System.out.println("---" + listClass.get(i).getListStudent().get(j).getName() + " ");
		}
	}

	public static void eagerLoading(Statement stmt) throws SQLException {
		listClass.clear();
		ResultSet rs = stmt.executeQuery(
				"Select c.id as class_id,c.name as class_name,s.id as student_id, s.name as student_name from Class c, Student s where c.id = s.class_id");
		
		// countClass Check if this class is in the listClass
		Map<Integer, String> countClass = new HashMap<Integer, String>();
		while (rs.next()) {
			// table is in the form: class_id, class_name, student_id, student_name
			// get infor of student first
			StudentEntity student = new StudentEntity();
			student.setId(rs.getLong(3));
			student.setName(rs.getString(4));
			
			// if this class doesn't count (not in the listClass)
			if (countClass.get((int) rs.getLong(1)) == null) {
				// create new classEntity
				ClassEntity classEntity = new ClassEntity();
				classEntity.setId(rs.getLong(1));
				classEntity.setName(rs.getString(2));
				listStudent = new ArrayList<StudentEntity>();
				listStudent.add(student);
				classEntity.setListStudent(listStudent);
				
				//add it to listClass
				listClass.add(classEntity);
				//tick it in countClass (this classEntity is in listClass)
				countClass.put((int) rs.getLong(1), rs.getString(2));
			} 
			// this class is in listClass
			else {
				listStudent = new ArrayList<StudentEntity>();
				// find where is it in listClass
				for (ClassEntity ce : listClass) {
					if (ce.getId() == rs.getLong(1)) {
						// add student in listStudent of this classEntity
						listStudent = ce.getListStudent();
						listStudent.add(student);
						ce.setListStudent(listStudent);
						break;
					}
				}
			}
		}
		// write to check
		for (int i = 0; i < listClass.size(); i++) {
			System.out.println("Student of class " + listClass.get(i).getName());
			for (int j = 0; j < listClass.get(i).getListStudent().size(); j++)
				System.out.println("---" + listClass.get(i).getListStudent().get(j).getName() + " ");
		}

	}
	
	// connect to mysql
	public static void ConnectionSQL() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/test", "root", "");
			Statement stmt = con.createStatement();
			System.out.println("=====Lazy Load");
			lazyLoad(stmt);
			System.out.println("=====Eager Load");
			eagerLoading(stmt);
			con.close();
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	public static void main(String[] args) {
		ConnectionSQL();
	}
}
