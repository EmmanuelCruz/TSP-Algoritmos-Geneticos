/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ia;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import processing.core.PApplet;
import processing.core.PFont;

import java.util.Random;

/**
 * Clase que muestra el laberinto gráficamente.
 * @author Emmanuel Cruz Hernández
 * @version Febrero 2019.
 */
public class TravellingProblem extends PApplet{

    PFont fuente;  // Fuente para mostrar texto en pantalla
    
    // Propiedades del modelo de la ventana.
    int alto = 15;         // Altura (en celdas) de la cuadricula.
    int ancho = 25;        // Anchura (en celdas) de la cuadricula.
    int celda = 40;          // Tamanio de cada celda cuadrada (en pixeles).
    Viaje model;  // El objeto que representa el modelo de caminos.
    
    @Override
    public void setup() {
        size( ancho*celda, (alto*celda)+32);
        background(50);
        fuente = createFont("Arial",12,true);
        model = new Viaje();
    }
    
    /**
     * Pintar el mundo del modelo (la cuadricula).
     */
    @Override
    public void draw() {
        stroke(0,0,0);
        fill(0,0,0);
        rect(0,0,ancho*celda,alto*celda);
        
        Camino c=model.mejorSolucion;
        
        int longitud=c.ciudades.size();
        
        City ciudad;
        City ciudad2;
        stroke(0,0,0);
        for(int k=0;k<longitud;k++){
            for(int l = 0; l < longitud; l++){
            ciudad=c.ciudades.get(k);
            ciudad2=c.ciudades.get(l);
            line((float)(ciudad.x), (float)(ciudad.y),
                 (float)(ciudad2.x), (float)(ciudad2.y));
            }
        }
        
        // Comienza a dibujar la solución del camino.
        
        stroke(0,255,0);
        fill(255,0,0);
        for(int i = 0; i < longitud; i++){
            ciudad=c.ciudades.get(i);
            rect((float)(ciudad.x/13)*645-700, (float)(ciudad.y)*-19 - 1650, 3, 3);
        }
        for(int j = 0; j < longitud-1; j++){
            ciudad=c.ciudades.get(j);
            ciudad2=c.ciudades.get(j+1);
            line((float)(ciudad.x/13)*645 - 700, (float)(ciudad.y)*-19 - 1650,
                 (float)(ciudad2.x/13)*645- 700, (float)(ciudad2.y)*-19 - 1650);
        }
        ciudad=c.ciudades.get(c.ciudades.size()-1);
        ciudad2=c.ciudades.get(0);
        line((float)(ciudad.x/13)*645-700, (float)(ciudad.y)*-19 - 1650,
             (float)(ciudad2.x/13)*645-700, (float)(ciudad2.y)*-19 - 1650);
        fill(50);
	rect(0, alto*celda, (ancho*celda), 32);
	fill(255);
        textFont(fuente,10);
	text("Cuadricula: " + ancho + " x " + alto, 5, (alto*celda)+12);
	text("Problema del Agente Viajero", 128, (alto*celda)+12);
        text("Aptitud del camino: "+c.aptitud, 328, (alto*celda)+12);
        text("Iteración: "+model.iteracion, 628, (alto*celda)+12);
        model.algoritmoGenetico(100);
    }
        
    
    // --- Clase Celda ---
    class City {
    
	// Identificador.
	int id;

	// Coordenada x.
	double x;

	// Coordenada y.
	double y;

	/**
	 * Crea una nueva ciudad con identificador y coordenadas.
	 * @param id el identificador inicial a asignar.
	 * @param x la coordenada x de la ciudad en el plano.
	 * @param y la coordenada y de la ciudad en el plano.
	 */
	public City(int id, double x, double y){
	    this.id=id;
	    this.x=x;
	    this.y=y;
	}

	/**
	 * Calcula la distancia entre dos ciudades.
	 * @param otra la ciudad a encontrar la distancia.
	 */
	public double calculaDistancia(City otra){
	    return Math.sqrt(Math.pow(otra.x-this.x,2)+Math.pow(otra.y-this.y,2));
	}

	@Override
	public String toString(){
	    return ""+id;
	}
    }
    
    /**
     * Clase auxiliar que representa un camino de un viajero.
     */
    class Camino{

	/** Lista que contiene a las ciudades. */
	public ArrayList<City> ciudades;

	/** Aptitud del fenotipo. */
	public int aptitud;

	private Random rnd=new Random();

	/**
	 * Crea un nuevo Camino.
	 */
	public Camino(){
	    // Se leen las ciudades y se asignan.
	    ArrayList<City> leidas=readCities();

	    // Se inicializa la lista de ciudades para asignar.
	    ciudades=new ArrayList<>();

	    // Se llena el camino de ciudades. Esto es un nuevo camino.
	    while(!leidas.isEmpty()){
		City nueva=leidas.get(rnd.nextInt(leidas.size()));
		ciudades.add(nueva);
		leidas.remove(nueva);
	    }

	    this.calculaAptitud();
	}

	/**
	 * Crea un Camino a partir de una lista ya dada.
	 * @param fen el nuevo fenotipo a asignar al objeto.
	 */
	public Camino(ArrayList<City> fen){
	    ciudades=fen;
	    this.calculaAptitud();
	}

	/**
	 * Método que se encarga de leer el archivo que contiene información de las ciudades.
	 * @return lista con las ciudades leídas del archivo.
	 */
	public ArrayList<City> readCities(){
	    ArrayList<City> l=new ArrayList<>();
	    try{
		//BufferedReader lect=new BufferedReader(new FileReader("Datos/Rwanda.txt"));
		//BufferedReader lect=new BufferedReader(new FileReader("Datos/Zimbabwe.txt"));
		//BufferedReader lect=new BufferedReader(new FileReader("Datos/Djibouti.txt"));
                BufferedReader lect=new BufferedReader(new FileReader("Datos/small3.txt"));
		String cityInfo;
		while((cityInfo=lect.readLine())!=null){
		    int a=cityInfo.indexOf(" ");
		    int b=cityInfo.indexOf(" ",a+1);
		    int identificador=Integer.parseInt(cityInfo.substring(0,a));
		    double x=Double.parseDouble(cityInfo.substring(a+1,b));
		    double y=Double.parseDouble(cityInfo.substring(b+1,cityInfo.length()));
		    City nuevo=new City(identificador,x,y);
		    l.add(nuevo);
		}
	    }catch(FileNotFoundException e){
		System.out.println("\nERROR: El archivo no fue encontrado");
		return null;
	    }catch(IOException e){
		System.out.println("\nERROR: Algo salió mal al intentar leer archivo.");
		return null;
	    }
	    return l;
	}

	/**
	 * Calcula la aptitud del Camino.
	 * @return la distancia de recorrer un país con el orden de las ciudades del objeto.
	 */
	public int calculaAptitud(){
	    int contador=0;
	    // Se hace una suma de cada una de la distancia de cada una de las ciudades que son adyacentes.
	    for(int i=0;i<ciudades.size()-1;i++)
		contador+=ciudades.get(i).calculaDistancia(ciudades.get(i+1));
	    // Se suma la ultima con la primera ciudad.
	    contador+=ciudades.get(ciudades.size()-1).calculaDistancia(ciudades.get(0));
	    // Se asigna la suma de las distancias como aptitud.
	    aptitud=contador;
	    return contador;
	}

	/**
	 * Función de recombinación.
	 * @param cam el Camino con el cuál aplicar la recombinación.
	 * @return un Camino resultante de la operación de recombinación.
	 */
	public Camino recombinacion(Camino cam){
	    ArrayList<City> leidas=readCities();
	    // Se define un corte random.
	    int division=rnd.nextInt(ciudades.size());
	
	    // Se crea una nueva lista de ciudades
	    ArrayList<City> nueva=new ArrayList<>();

	    // Se llena la lista con las ciudades de ambas listas.
	    for(int i=0;i<ciudades.size();i++){
		if(i<=division){
		    City agregada=ciudades.get(i);
		    nueva.add(agregada);
		    eliminaCiudad(leidas,agregada);
		    //leidas.remove(agregada);
		} else{
		    City agregada2=cam.ciudades.get(i);
		    //if(!nueva.contains(agregada2)){
		    if(!estaCiudad(nueva,agregada2)){
			nueva.add(agregada2);
			eliminaCiudad(leidas,agregada2);
			//leidas.remove(agregada2);
		    }
		}
	    }

	    // Se llena el camino de ciudades restantes. Esto es un nuevo camino.
	    while(!leidas.isEmpty()){
		City nueva1=leidas.get(rnd.nextInt(leidas.size()));
		nueva.add(nueva1);
		leidas.remove(nueva1);
	    }

	    return new Camino(nueva);
	}

	/**
	 * Función de mutación.
	 */
	public void mutacion(){
	    // Si se cumple la probabilidad de 0.2 se realiza un cambio entre 2 ciudades.
	    if(0.4>rnd.nextDouble()){
		int elemento1=rnd.nextInt(ciudades.size());
		int elemento2=rnd.nextInt(ciudades.size());
		while(elemento1==elemento2){
		    elemento1=rnd.nextInt(ciudades.size());
		    elemento2=rnd.nextInt(ciudades.size());
		}
		// Identifico cuál es el elemento mayor y menor, respectivamente.
		int mayor=elemento1>elemento2?elemento1:elemento2;
		int menor=elemento1<elemento2?elemento1:elemento2;

		City ciudad1=ciudades.remove(mayor);
		City ciudad2=ciudades.remove(menor);

		ciudades.add(menor,ciudad1);
		ciudades.add(mayor,ciudad2);
		this.calculaAptitud();
	    }	
	}

	/**
	 * Verifica si una ciudad se encuentra en una lista de ciudades. Se busca por su identificador.
	 * @param lista la lista de ciudades en la cual buscar.
	 * @param ciudad la ciudad a verificar si está en la lista.
	 * @return true si el elemento está en la lista, false en otro caso.
	 */
	private boolean estaCiudad(ArrayList<City> lista, City ciudad){
	    int tam=lista.size();
	    for(int i=0;i<tam;i++)
		if(lista.get(i).id==ciudad.id)
		    return true;
	    return false;
	}

	/**
	 * Elimina una ciudad de una lista de ciudades. Se elimina por su identificador.
	 * @param lista la lista de ciudades en la cual eliminar.
	 * @param ciudad la ciudad a eliminar de la lista.
	 */
	private void eliminaCiudad(ArrayList<City> lista, City ciudad){
	    int tam=lista.size();
	    for(int i=0;i<tam;i++)
		if(lista.get(i).id==ciudad.id){
		    lista.remove(i);
		    return;
		}
	}

	@Override
	public String toString(){
	    String representacion="[ ";
            for (City ciudade : ciudades) {
                representacion += ciudade + " ";
            }
	    representacion+="]\n\n";

	    representacion+="Aptitud = "+aptitud+".\n";
	    return representacion;
	}

    }

    /**
     * Clase en la que se implementa el uso de los algoritmos genéticos
     * para resolver el problema del viajero.
     * @author Emmanuel Cruz Hernández. 314272588.
     * @version 0.1 Junio.
     */
    class Viaje{

	/** Población de individuos (Camino). */
	public Camino[] poblacion=new Camino[50];
        
        /** Mejor solución encontrada hasta el momento. */
        public Camino mejorSolucion;

        /** Iteración al ejecutar el algoritmo genetico. */
        public int iteracion;
        
	/**
	 * Crea un nuevo objeto Viaje.
	 */
	public Viaje(){
	    for(int i=0;i<poblacion.length;i++)
		poblacion[i]=new Camino();
            mejorSolucion=this.daMejorSolucion();
            iteracion=0;
	}

	/**
	 * Calcula la suma total de aptitudes de cada elemento de la población.
	 * @return el total de las sumas de aptitudes.
	 */
	public double totalFitness(){
	    double total=0;
            for (Camino poblacion1 : poblacion) {
                total += poblacion1.aptitud;
            }
	    return total;
	}

	/**
	 * Selecciona un Camino (individuo) de la población a partir de cierta probabilidad.
	 * @return un Camino seleccionado de la población.
	 */
	public Camino selecciona(){
	    Random rnd=new Random();
	    Camino selecionado=null;
	    // Obtenemos la suma de todos las aptitudes de la población.
	    double total=this.totalFitness();
	    int recorredor=0;
	    // Mientras no se haya seleccionado algún elemento de la población se busca alguno.
	    while(selecionado==null){
		if(poblacion[recorredor%poblacion.length].aptitud/total > rnd.nextDouble())
		    selecionado=poblacion[recorredor%poblacion.length];
		recorredor++;
	    }
	    return selecionado;
	}

	/**
	 * Regresa la primera mejor solución encontrada entre los individuos de la población.
	 * @return el Camino con la aptitud de mejor solución.
	 */
	public Camino daMejorSolucion(){
	    int mejor=0;
	    for(int i=0;i<poblacion.length;i++)
		// Si la aptitud del elemento donde está el iterador es menor a la mejor encontrada hasta el momento se cambia.
		if(poblacion[i].aptitud < poblacion[mejor].aptitud){
		    mejor=i;
		    // Si encontramos alguno que ya tiene aptitud 0 entonces lo regresamos.
		    if(poblacion[mejor].aptitud==0)
			return poblacion[mejor];
		}
	    return poblacion[mejor];
	}

	/**
	 * Algoritmo genetico para encontrar la mejor solución al problema del viajero.
	 * @param n la cantidad de iteraciones para llevar a cabo el algoritmo.
	 * @return el Camino con la mejor solución encontrada.
	 */
	public Camino algoritmoGenetico(int n){
	    int iterador=0;
	    int lleno=0;
	    // Realizamos esta operación n veces.
	    while(iterador<n){
		// Se crea una nueva población.
		Camino[] nuevaPoblacion=new Camino[poblacion.length];
		// Agregamos la mejor solución que este en la población actual.
		nuevaPoblacion[0]=this.daMejorSolucion();
		lleno=1;
		while(lleno<nuevaPoblacion.length){
		    // Creamos 2 nuevos Caminos (individuos)
		    Camino c1=this.selecciona();
		    Camino c2=this.selecciona();
		    // De estos creamos uno nuevo a partir de la operación de recombinación.
		    Camino hijo=c1.recombinacion(c2);
		    // Este nuevo que creamos lo mutamos para cambiar algunos valores si se da el caso.
		    hijo.mutacion();
		    // Lo agregamos a la nueva población
		    nuevaPoblacion[lleno]=hijo;
		    lleno++;
		}
		// Atualizmos la población actual por la nueva.
		poblacion=nuevaPoblacion;
		Camino best=this.daMejorSolucion();
		// Cada 50 iteraciones se imprime el resultado hasta el momento.
		if(iterador%50==0){
                    mejorSolucion=new Camino(best.ciudades);
		    System.out.println("ITERACIÓN "+iteracion+"\n"+best);
                }
		iterador++;
                iteracion++;
	    }
	    // Regresamos la mejor solución después de n iteraciones.
	    return this.daMejorSolucion();
	}

	/*public static void main(String[] args){
	  Viaje tn=new Viaje();
	  int iteraciones=200000;
	  Camino solucionFinal=tn.algoritmoGenetico(iteraciones);

	  System.out.println("MEJOR SOLUCIÓN ENCONTRADA\n"+solucionFinal);

	  }*/

    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
	PApplet.main(new String[] { "ia.TravellingProblem" });
    }
    
}
