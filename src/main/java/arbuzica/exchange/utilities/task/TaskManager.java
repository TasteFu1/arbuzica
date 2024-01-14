package arbuzica.exchange.utilities.task;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;

public class TaskManager {
    private final List<Task> taskList = new ArrayList<>();

    public TaskManager newTask(Task task) {
        taskList.add(task);
        return this;
    }

    public TaskManager tick() {
        ForkJoinPool.commonPool().execute(() -> {
            while (true) {
                taskList.forEach(Task::execute);
            }
        });

        return this;
    }
}
