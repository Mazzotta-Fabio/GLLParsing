package gllalgoritmobase;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Scanner;
import stack.*;

/*
 * GRAMMATICA
 * S->ASd
 * S->BS
 * S->epsilon
 * A->a
 * A->c
 * B->a
 * B->b
 */
public class ParserBaseStack {
	
	public static void main(String []args){
		File f=new File("file.txt");
		Scanner buffer;
		if(f.exists()){
			try{
				FileReader in=new FileReader(f);
				buffer=new Scanner(in);
				char[] buf;
				while(buffer.hasNextLine()){
					String line=buffer.nextLine();
					buf=line.toCharArray();
					String esito=parse(buf);
					System.out.println(esito);
				}	
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
		else{
			System.out.println("File not Found");
		} 
	}
	
	public static String parse(char []buf){
		//dichirazione indici
		int i=0;int j=i;
		//dichirazione insieme r usato per registrare le scelte del non determinismo
		ArrayList<Tripla> r=new ArrayList<Tripla>();
		//dichirazione stack
		Stack<String> s=new ArrayStack<String>();
		Stack<String> lasts;
		//inizializzo lo stack con il primo stato
		s.push("Ls0L0");
		//inizializzo etichette
		String etichetta ="LS";
		String lastlabel;
		String top;
		while(true){
			switch(etichetta){
			//OK
			case "LS":
				if((buf[i]=='a')||(buf[i]=='c')){
					r.add(new Tripla("LS1",s.duplica(),i));
				}
				if((buf[i]=='a')||(buf[i]=='b')){
					r.add(new Tripla("LS2",s.duplica(),i));
				}
				if((buf[i]=='d')||(buf[i]=='$')){
					r.add(new Tripla("LS3",s.duplica(),i));
				}
				etichetta="L0";
				break;
			//OK
			case "L0":
				if(r.size()>0){
					lastlabel=r.get(0).getEtichetta();
					j=r.get(0).getI();
					lasts=r.get(0).getS();
					System.out.println(r.get(0));
					r.remove(0);
					if((lastlabel.equals("L0"))&&(s.size()==0)&&(j==(buf.length-1))){
						return "SUCCESSO";
					}
					else{
						s=lasts;
						i=j;
						etichetta=lastlabel;
					}
				}
				else{
					return "NON SUCCESSO";
				}
				break;
			//ok
			case "LS1":
				s.push("Ls"+i+"L1");
				etichetta="La";
				break;
			//ok
			case "LS2":
				s.push("Ls"+i+"L3");
				etichetta="Lb";
				break;
			case "LS3":
				top=s.pop().substring(3);
				r.add(new Tripla(top,s.duplica(),i));
				etichetta="L0";
				break;
			case "L1":
				s.push("Ls"+i+"L2");
				etichetta="LS";
				break;
			case "L2":
				if(buf[i]=='d'){
					i++;
					top=s.pop().substring(3);
					r.add(new Tripla(top,s.duplica(),i));
				}
				etichetta="L0";
				break;
			case "L3":
				s.push("Ls"+i+"L4");
				etichetta="LS";
				break;
			case "L4":
				top=s.pop().substring(3);
				r.add(new Tripla(top,s.duplica(),i));
				etichetta="L0";
				break;
			case "La":
				if(buf[i]=='a'){
					i++;
					top=s.pop().substring(3);
					r.add(new Tripla(top,s.duplica(),i));
					etichetta="L0";
				}
				else{
					if(buf[i]=='c'){
						i++;
						top=s.pop().substring(3);
						r.add(new Tripla(top,s.duplica(),i));
					}
					etichetta="L0";
				}
				break;
			case "Lb":
				if(buf[i]=='a'){
					i++;
					top=s.pop().substring(3);
					r.add(new Tripla(top,s.duplica(),i));
					etichetta="L0";
				}
				else{
					if(buf[i]=='b'){
						i++;
						top=s.pop().substring(3);
						r.add(new Tripla(top,s.duplica(),i));
					}
					etichetta="L0";
				}
				break;
			}
		}
	}
}