import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import java.util.ArrayList;
import java.util.List;
import static org.quartz.JobBuilder.*;
import static org.quartz.TriggerBuilder.*;
import static org.quartz.SimpleScheduleBuilder.*;

public class AlertRabbit {
    public AlertRabbit() {
    }

    public static void main(String[] args) {
        try {
            String path = "src/main/resources/rabbit.properties";
            List<Long> store = new ArrayList<>();
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();
            JobDataMap data = new JobDataMap();
            data.put("store", store);
            JobDetail job = newJob(Rabbit.class)
                    .usingJobData(data)
                    .build();
            SimpleScheduleBuilder times = simpleSchedule()
                    .withIntervalInSeconds(readConfig(path))
                    .repeatForever();
            Trigger trigger = newTrigger()
                    .startNow()
                    .withSchedule(times)
                    .build();
            scheduler.scheduleJob(job, trigger);
            Thread.sleep(10000);
            scheduler.shutdown();
            System.out.println(store);
        } catch (Exception se) {
            se.printStackTrace();
        }
    }

    public static int readConfig(String path) {
        ru.job4j.io.Config config = new ru.job4j.io.Config(path);
        config.load();
        return Integer.parseInt(config.value("rabbit.interval"));
    }

    public static class Rabbit implements Job {

        public Rabbit() {
            System.out.println(hashCode());
        }
        @Override
        public void execute(JobExecutionContext context)
                throws JobExecutionException {
            System.out.println("Rabbit runs here ...");
            List<Long> store = (List<Long>) context
                    .getJobDetail()
                    .getJobDataMap()
                    .get("store");
            store.add(System.currentTimeMillis());
        }
    }
}
