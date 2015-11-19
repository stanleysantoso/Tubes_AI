import java.util.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.BufferedInputStream;  
import java.io.DataInputStream;  

class tableString{
	private Vector<String> Tab;
	
	public tableString(){
		Vector<String> Tab = new Vector<>();
	}
	public Vector<String> getTab(){
		return Tab;
	}
	public void setTab(Vector<String> T){
		Tab = T;
	}
	public int isFalse(String S){//menghasilkan 1 jika benar dan nol jika salah
		int i = 0;
		int j = 0;
		while(i < Tab.size()){
			if(Tab.elementAt(i).equals(S)){
				if(Tab.elementAt(Tab.size()-1).equals("f")||Tab.elementAt(Tab.size()-1).equals("F")||Tab.elementAt(Tab.size()-1).equals("FALSE")||Tab.elementAt(Tab.size()-1).equals("false")){
					j = 1;
				}
			}
			i++;
		}
		return j;
	}
	public int isTrue(String S){//menghasilkan 1 jika benar dan nol jika salah
		int i = 0;
		int j = 0;
		while(i < Tab.size()){
			if(Tab.elementAt(i).equals(S)){
				if(Tab.elementAt(Tab.size()-1).equals("T")||Tab.elementAt(Tab.size()-1).equals("t")||Tab.elementAt(Tab.size()-1).equals("true")||Tab.elementAt(Tab.size()-1).equals("TRUE")){
					j = 1;
				}
			}
			i++;
		}
		return j;
	}
	public int isFalse(){//menghasilkan 1 jika benar dan nol jika salah
		int i = 0;
		int j = 0;
		
		if(Tab.elementAt(Tab.size()-1).equals("f")||Tab.elementAt(Tab.size()-1).equals("F")||Tab.elementAt(Tab.size()-1).equals("FALSE")||Tab.elementAt(Tab.size()-1).equals("false")){
			j = 1;
		}
		return j;
	}
	public int isTrue(){//menghasilkan 1 jika benar dan nol jika salah
		int i = 0;
		int j = 0;
		
		if(Tab.elementAt(Tab.size()-1).equals("T")||Tab.elementAt(Tab.size()-1).equals("t")||Tab.elementAt(Tab.size()-1).equals("true")||Tab.elementAt(Tab.size()-1).equals("TRUE")){
			j = 1;
		}
		return j;
	}
	public void printTable(){
		for(int i = 0; i < Tab.size(); i++){
			System.out.println(Tab.elementAt(i));
		}
	}
};

class naiveBayes{
	private Vector<tableString> Tab;
	
	public naiveBayes(){
		Vector<tableString> Tab = new Vector<>();
	}
	public Vector<tableString> getTab(){
		return Tab;
	}
	public tableString getTab(int i){
		return Tab.elementAt(i);
	}	
	public void setTab(Vector<tableString> T){
		Tab = T;
	}
	public int cariPeluangTrue(){
		int i = 0;
		for(int j = 0; j < Tab.size(); j++){
			if(Tab.elementAt(j).isTrue() == 1){
				i++;
			}
		}
		return i;
	}
	public int cariPeluangFalse(){
		int i = 0;
		for(int j = 0; j < Tab.size(); j++){
			if(Tab.elementAt(j).isFalse() == 1){
				i++;
			}
		}
		return i;
	}
	public int cariPeluangSesuatuTrue(String S){
		int i = 0;
		for(int j = 0; j < Tab.size(); j++){
			if(Tab.elementAt(j).isTrue(S) == 1){
				i++;
			}
		}
		return i;
	}
	public int cariPeluangSesuatuFalse(String S){
		int i = 0;
		for(int j = 0; j < Tab.size(); j++){
			if(Tab.elementAt(j).isFalse(S) == 1){
				i++;
			}
		}
		return i;
	}
	public double NilaiDariPeluangTrue(Vector<String> A){
		int temp = 0, temp1 = 0;
		double x = 0,peltrue = 0;
		temp = cariPeluangTrue();
		temp1 = cariPeluangFalse();
		x  = (double)(temp + temp1);
		peltrue = temp/x;
		if(temp != 0){
		for(int i = 0; i < Tab.size()-1; i++){
			peltrue *= (cariPeluangSesuatuTrue(A.elementAt(i))/temp);
		}
		}
		return peltrue;
	}
	public double NilaiDariPeluangFalse(Vector<String> A){
		int temp = 0, temp1 = 0;
		double x = 0,pelfalse = 0;
		temp = cariPeluangTrue();
		temp1 = cariPeluangFalse();
		x  = (double)(temp + temp1);
		pelfalse = temp1/x;
		if(temp1 != 0){
		for(int i = 0; i < Tab.size()-1; i++){
			pelfalse *= (cariPeluangSesuatuFalse(A.elementAt(i))/temp1);
		}
		}
		return pelfalse;
	}
	public void CariNaiveBayes(Vector<String> S){
		FileInputStream finput1 = null;
		BufferedInputStream bis1 = null;
		DataInputStream dis1 = null;
		String s = new String();
		String s1 = new String();
		int retval1;
		Vector<tableString> Tab1 = new Vector<>();
		try {
			finput1 = new FileInputStream ("INPUT.txt");
			bis1 = new BufferedInputStream(finput1);
			dis1 = new DataInputStream(bis1);
		}catch (FileNotFoundException fnfe){
			System.out.println("File tidak ditemukan.");
			return;
		}
		try{
			while ((retval1 = finput1.read()) != -1){
				while (dis1.available() != 0) {
					s1 = dis1.readLine();
					StringTokenizer st = new StringTokenizer(s1);
					s = (String)st.nextElement();
					Vector<String> Contoh = new Vector<>();
					tableString TC = new tableString();
					while(!s.equals(".")){
						if (!s.equals(",")){
							Contoh.add(s);
						}
						s = (String)st.nextElement();
					}
					TC.setTab(Contoh);
					Tab1.add(TC);
				}
			}
			for(int i = 0; i < Tab1.size(); i++){
				System.out.println("pp");
				Tab1.elementAt(i).printTable();
			}
		}
		catch (IOException ioe) {
			System.out.println(ioe.getMessage());
			return;
		}
		setTab(Tab1);
		if(NilaiDariPeluangTrue(S) >= NilaiDariPeluangFalse(S)){
			System.out.println("T");
		}else{
			System.out.println("F");
		}
	}
	public static void main(String []args){
		
		naiveBayes kn = new naiveBayes();
		Vector<String> Contoh2 = new Vector<>();
		Contoh2.add("f");
		Contoh2.add("t");
		Contoh2.add("f");
		kn.CariNaiveBayes(Contoh2);
	}
}