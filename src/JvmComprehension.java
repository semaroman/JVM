public class JvmComprehension {

    public static void main(String[] args) {
        int i = 1;                      // 1
        Object o = new Object();        // 2
        Integer ii = 2;                 // 3
        printAll(o, i, ii);             // 4
        System.out.println("finished"); // 7
    }

    private static void printAll(Object o, int i, Integer ii) {
        Integer uselessVar = 700;                   // 5
        System.out.println(o.toString() + i + ii);  // 6
    }
}

// При создании класса JvmComprehension он отправляется для загрузки в систему ClassLoaders.
// Далее происходит запрос на класс JvmComprehension и обращение к подсистеме Application
// ClassLoader, которая передаёт его в Platform ClassLoader, которая, в свою очередь, передаёт его
// дальше - в Bootstrap ClassLoader. Далее происходит поиск класса в пакетах (java.util, java.lang и т.д.), в
// нашем случае этот класс не находится там, и запрос переходит на уровень Platform ClassLoader, далее происходит
// поиск класса в подключённых библиотеках, где он также не присутствует, так что запрос возвращается в
// Application ClassLoader, где проверяются все написанные нами классы и загружается наш класс JvmComprehension.
// Далее происходит связывание - подготовка класса к выполнению: код проверяется на валидность и подготовка
// примитивов в статических полях.

// После этого выполняется инициализация класса JvmComprehension и информация о нём помещается в Metaspace.
// Для метода main() создаётся фрейм в StackMemory. Переменная int i создаётся в StackMemory в фрейме main (1).
// Объект класса Object создаётся с вызовом конструктора без параметров в heap, а ссылка на него создаётся в
// StackMemory во фреймах main и printAll (2). Переменная Integer ii создаётся в StackMemory в фрейме main() (3).
// Для метода printAll() создаётся фрейм в StackMemory, и при передаче в этот метод объекта o создаётся ссылка
// на этот объект, находящийся в heap, и при передаче в метод переменных i и ii, лежащих во фрейме main, также
// создаются ссылки на них (4). Переменная Integer uselessVar i создаётся в StackMemory во фрейме printAll (5).
// Для метода o.toString() выделяется место в heap и создаётся объект класса String, далее вызывается системный
// метод println и для него в StackMemory создаётся новый фрейм, в котором появятся ссылки на данный объект, а
// также на переменные i и ii (6). При вызове метода println для него создаётся новый фрейм в StackMemory, а для
// надписи "finished" создаётся объект класса String в heap, на который потом ссылается println (7).

// Сборщик мусора определяет переменную uselessVar как недосягаемый объект методом обхода графа достижимых
// объектов (mark-and-sweep, copying collection) и удаляет её из фрейма printAll.