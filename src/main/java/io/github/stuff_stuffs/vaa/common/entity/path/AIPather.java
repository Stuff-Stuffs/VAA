package io.github.stuff_stuffs.vaa.common.entity.path;

import io.github.stuff_stuffs.vaa.common.entity.AIEntity;
import io.github.stuff_stuffs.vaa.common.util.WorldCache;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.objects.ObjectHeapPriorityQueue;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.commons.lang3.time.StopWatch;

import java.util.Comparator;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class AIPather {
    private final AIPathNode[] successors = new AIPathNode[64];
    private final AIEntity aiEntity;
    private final World world;
    private final NodeProducer nodeProducer;

    public AIPather(final AIEntity aiEntity, final World world, final NodeProducer nodeProducer) {
        this.aiEntity = aiEntity;
        this.world = world;
        this.nodeProducer = nodeProducer;
    }

    public NodeProducer getNodeProducer() {
        return nodeProducer;
    }

    public CompletableFuture<AIPath> calculatePath(final PathTarget pathTarget, final double max, final boolean partial) {
        final WorldCache cache = new WorldCache(world, aiEntity.getBlockPos().add(-256, -256, -256), aiEntity.getBlockPos().add(256, 256, 256));
        final AIPathNode start = nodeProducer.getStart(cache);
        return CompletableFuture.supplyAsync(() -> {
            final StopWatch stopWatch = StopWatch.createStarted();
            final double err = pathTarget.getRadius();
            //TODO specialized heap implementation
            final ObjectHeapPriorityQueue<AIPathNode> queue = new ObjectHeapPriorityQueue<>(Comparator.comparingDouble(i -> i.distToTarget));
            final LongSet visited = new LongOpenHashSet();
            double bestDist = Double.POSITIVE_INFINITY;
            AIPathNode best = null;
            start.distToTarget = pathTarget.heuristic(start.x, start.y, start.z);
            queue.enqueue(start);
            visited.add(BlockPos.asLong(start.x, start.y, start.z));
            while (!queue.isEmpty()) {
                final AIPathNode current = queue.dequeue();
                if (current.distance > max) {
                    continue;
                }
                if (current.distToTarget < bestDist) {
                    bestDist = current.distToTarget;
                    best = current;
                }
                if (current.previous != null) {
                    current.nodeCount = current.previous.nodeCount + 1;
                } else {
                    current.nodeCount = 1;
                }
                if (pathTarget.heuristic(current.x, current.y, current.z) < err) {
                    System.out.println("Time: " + stopWatch.getTime(TimeUnit.NANOSECONDS) / 1_000_000D);
                    System.out.println("Nodes considered: " + visited.size());
                    nodeProducer.stats();
                    return toPath(current);
                }
                final int count = nodeProducer.getNeighbours(current, successors);
                for (int i = 0; i < count; i++) {
                    final AIPathNode next = successors[i];
                    final long pos = BlockPos.asLong(next.x, next.y, next.z);
                    if (visited.add(pos)) {
                        next.distToTarget = pathTarget.heuristic(next.x, next.y, next.z);
                        queue.enqueue(next);
                    }
                }
            }
            stopWatch.stop();
            final double v = stopWatch.getTime(TimeUnit.NANOSECONDS) / 1_000_000D;
            System.out.println("Time: " + v);
            System.out.println("Nodes considered: " + visited.size());
            System.out.println("N/S: " + (visited.size() / (v / 1000)));
            nodeProducer.stats();
            if (partial && best != null) {
                return toPath(best);
            }
            return null;
        });
    }

    private static AIPath toPath(AIPathNode node) {
        final AIPathNode[] nodes = new AIPathNode[node.nodeCount];
        for (int i = nodes.length - 1; i >= 0; i--) {
            nodes[i] = node;
            node = node.previous;
        }
        final boolean[] canErase = new boolean[nodes.length];
        for (int i = 1; i < nodes.length - 1; i++) {
            final AIPathNode cur = nodes[i];
            final AIPathNode next = nodes[i + 1];
            final AIPathNode prev = nodes[i - 1];
            int match = 0;
            if (prev.x == cur.x && cur.x == next.x) {
                match++;
            }
            if (prev.z == cur.z && cur.z == next.z) {
                match++;
            }
            if (prev.y == cur.y && cur.y == next.y) {
                match++;
            }
            canErase[i] = match > 1;
        }
        for (int i = 0; i < canErase.length; i++) {
            if (canErase[i]) {
                nodes[i] = null;
            }
        }
        return new AIPath(nodes);
    }
}
