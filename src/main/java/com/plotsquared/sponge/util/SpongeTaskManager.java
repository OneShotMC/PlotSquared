package com.plotsquared.sponge.util;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.spongepowered.api.service.scheduler.Task;
import org.spongepowered.api.service.scheduler.TaskBuilder;

import com.intellectualcrafters.plot.util.TaskManager;
import com.plotsquared.sponge.SpongeMain;

public class SpongeTaskManager extends TaskManager
{

    private final AtomicInteger i = new AtomicInteger();

    private final HashMap<Integer, Task> tasks = new HashMap<>();

    @Override
    public int taskRepeat(final Runnable r, final int interval)
    {
        final int val = i.incrementAndGet();
        final TaskBuilder builder = SpongeMain.THIS.getGame().getScheduler().createTaskBuilder();
        final TaskBuilder built = builder.delay(interval).interval(interval).execute(r);
        final Task task = built.submit(SpongeMain.THIS.getPlugin());
        tasks.put(val, task);
        return val;
    }

    @Override
    public int taskRepeatAsync(final Runnable r, final int interval)
    {
        final int val = i.incrementAndGet();
        final TaskBuilder builder = SpongeMain.THIS.getGame().getScheduler().createTaskBuilder();
        final TaskBuilder built = builder.delay(interval).async().interval(interval).execute(r);
        final Task task = built.submit(SpongeMain.THIS.getPlugin());
        tasks.put(val, task);
        return val;
    }

    @Override
    public void taskAsync(final Runnable r)
    {
        final TaskBuilder builder = SpongeMain.THIS.getGame().getScheduler().createTaskBuilder();
        builder.async().execute(r).submit(SpongeMain.THIS.getPlugin());
    }

    @Override
    public void task(final Runnable r)
    {
        final TaskBuilder builder = SpongeMain.THIS.getGame().getScheduler().createTaskBuilder();
        builder.execute(r).submit(SpongeMain.THIS.getPlugin());
    }

    @Override
    public void taskLater(final Runnable r, final int delay)
    {
        final TaskBuilder builder = SpongeMain.THIS.getGame().getScheduler().createTaskBuilder();
        builder.delay(delay).execute(r).submit(SpongeMain.THIS.getPlugin());
    }

    @Override
    public void taskLaterAsync(final Runnable r, final int delay)
    {
        final TaskBuilder builder = SpongeMain.THIS.getGame().getScheduler().createTaskBuilder();
        builder.async().delay(delay).execute(r).submit(SpongeMain.THIS.getPlugin());
    }

    @Override
    public void cancelTask(final int i)
    {
        final Task task = tasks.remove(i);
        if (task != null)
        {
            task.cancel();
        }
    }

}
