// Monitor que implementa a logica do padrao leitores/escritores
class Varia {
  public static int x = 0; //cria a variável global compartilhada
}
class LE {
  private int leit, escr;

  // Construtor
  LE() {
     this.leit = 0; //leitores lendo (0 ou mais)
     this.escr = 0; //escritor escrevendo (0 ou 1)
  }

  // Entrada para leitores
  public synchronized void EntraLeitor (int id) {
    try {
      while (this.escr > 0) {
      //if (this.escr > 0) {
         System.out.println ("le.leitorBloqueado("+id+")");
         wait();  //bloqueia pela condicao logica da aplicacao
      }
      this.leit++;  //registra que ha mais um leitor lendo
      System.out.println ("le.leitorLendo("+id+")");
    } catch (InterruptedException e) { }
  }

  // Saida para leitores
  public synchronized void SaiLeitor (int id) {
     this.leit--; //registra que um leitor saiu
     if (this.leit == 0)
           this.notify(); //libera escritor (caso exista escritor bloqueado)
     System.out.println ("le.leitorSaindo("+id+")");
  }

  // Entrada para escritores
  public synchronized void EntraEscritor (int id) {
    try {
      while ((this.leit > 0) || (this.escr > 0)) {
      //if ((this.leit > 0) || (this.escr > 0)) {
         System.out.println ("le.escritorBloqueado("+id+")");
         wait();  //bloqueia pela condicao logica da aplicacao
      }
      this.escr++; //registra que ha um escritor escrevendo
      System.out.println ("le.escritorEscrevendo("+id+")");
    } catch (InterruptedException e) { }
  }

  // Saida para escritores
  public synchronized void SaiEscritor (int id) {
     this.escr--; //registra que o escritor saiu
     notifyAll(); //libera leitores e escritores (caso existam leitores ou escritores bloqueados)
     System.out.println ("le.escritorSaindo("+id+")");
  }
}



//Aplicacao de exemplo--------------------------------------------------------
// Leitor
class Leitor extends Thread {
  int id; //identificador da thread
  int delay; //atraso bobo
  LE monitor;//objeto monitor para coordenar a lógica de execução das threads

  // Construtor
  Leitor (int id, int delayTime, LE m) {
    this.id = id;
    this.delay = delayTime;
    this.monitor = m;
  }

  // Método executado pela thread
  public void run () {
    double j=777777777.7, i;
    try {
      for (;;) {
        this.monitor.EntraLeitor(this.id);
        if((Varia.x%2)==0){ //le a variavel
          System.out.println ("Variável: " +  Varia.x + " é par"); //fala se é par
        }else{
          System.out.println ("A Variável: " +  Varia.x + " é ímpar"); //fala se é impar
        }
        for (i=0; i<100000000; i++) {j=j/2;} //...loop bobo para simbolizar o tempo de leitura
        this.monitor.SaiLeitor(this.id);
        sleep(this.delay);
      }
    } catch (InterruptedException e) { return; }
  }
}

//--------------------------------------------------------
// Escritor
class Escritor extends Thread {
  int id; //identificador da thread
  int delay; //atraso bobo...
  LE monitor; //objeto monitor para coordenar a lógica de execução das threads

  // Construtor
  Escritor (int id, int delayTime, LE m) {
    this.id = id;
    this.delay = delayTime;
    this.monitor = m;
  }


  // Método executado pela thread
  public void run () {
    double j=777777777.7, i;
    try {
      for (;;) {
        this.monitor.EntraEscritor(this.id);
        Varia.x++; //... incrementa 1
        for (i=0; i<100000000; i++) {j=j/2;}
        this.monitor.SaiEscritor(this.id);
        sleep(this.delay); //atraso bobo...
      }
    } catch (InterruptedException e) { return; }
  }
}

  //--------------------------------------------------------
  // Leitor e Escritor
  class LeitoreEscritor extends Thread {
    int id; //identificador da thread
    int delay; //atraso bobo
    LE monitor;//objeto monitor para coordenar a lógica de execução das threads

    // Construtor
    LeitoreEscritor (int id, int delayTime, LE m) {
      this.id = id;
      this.delay = delayTime;
      this.monitor = m;
    }

    // Método executado pela thread
    public void run () {
      double j=777777777.7, i;
      try {
        for (;;) {
          this.monitor.EntraLeitor(this.id);
          System.out.println ("Variável:" +  Varia.x + "."); //primeiro lê a variável
          for (i=0; i<100000000; i++) {j=j/2;} //...loop bobo
          this.monitor.SaiLeitor(this.id);
          sleep(this.delay);
          this.monitor.EntraEscritor(this.id);
          Varia.x=id;// seta id na variável    
          this.monitor.SaiEscritor(this.id);

        }
      } catch (InterruptedException e) { return; }
    }
  }
//--------------------------------------------------------
// Classe principal
class LeitorEscritor {
  static final int L = 4;
  static final int E = 3;
  static final int EL = 3;

  public static void main (String[] args) {
    int i;
    LE monitor = new LE();            // Monitor (objeto compartilhado entre leitores e escritores)
    Leitor[] l = new Leitor[L];       // Threads leitores
    Escritor[] e = new Escritor[E];   // Threads escritores
    LeitoreEscritor[] el = new LeitoreEscritor[EL];   // Threads  leitores e escritores

    //inicia o log de saida
    System.out.println ("import verificaLE");
    System.out.println ("le = verificaLE.LE()");

    for (i=0; i<L; i++) {
       l[i] = new Leitor(i+1, (i+1)*500, monitor);
       l[i].start();
    }
    for (i=0; i<E; i++) {
       e[i] = new Escritor(i+1, (i+1)*500, monitor);
       e[i].start();
    }
    for (i=0; i<EL; i++) {
      el[i] = new LeitoreEscritor(i+1, (i+1)*500, monitor);
      el[i].start();
   }
  }
}
