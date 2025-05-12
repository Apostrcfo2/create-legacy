package nl.melonstudios.create.kinetics;

import nl.melonstudios.create.tileentity.TileEntityKinetic;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class KineticNetwork {
    public Long networkID = null;
    public boolean initialized = false;

    public Map<TileEntityKinetic, Float> members = new HashMap<>();
    public Map<TileEntityKinetic, Float> sources = new HashMap<>();

    private float capacity, stress;
    private float unloadedCapacity, unloadedStress;
    private int unloadedMembers;

    public KineticNetwork() {

    }

    public void initFromTE(float capacity, float stress, int members) {
        this.unloadedCapacity = capacity;
        this.unloadedStress = stress;
        this.unloadedMembers = members;
        this.initialized = true;
    }

    public void addSilently(TileEntityKinetic te, float lastCapacity, float lastStress) {
        if (this.members.containsKey(te)) return;
        if (te.isSource()) {
            this.unloadedCapacity -= lastCapacity * Math.abs(te.getGeneratedSpeed());
            this.sources.put(te, te.calculateCapacity());
        }

        this.unloadedStress -= lastStress * Math.abs(te.getTheoreticalSpeed());
        this.members.put(te, te.calculateImpact());

        this.unloadedMembers--;
        if (this.unloadedMembers < 0) this.unloadedMembers = 0;
        if (this.unloadedCapacity < 0) this.unloadedCapacity = 0;
        if (this.unloadedStress < 0) this.unloadedStress = 0;
    }

    public void add(TileEntityKinetic te) {
        if (this.members.containsKey(te)) return;
        if (te.isSource()) this.sources.put(te, te.calculateCapacity());
        this.members.put(te, te.calculateImpact());
        te.networkDirty = true;
    }

    public void updateCapacityFor(TileEntityKinetic te, float capacity) {
        this.sources.put(te, capacity);
    }
    public void updateImpactFor(TileEntityKinetic te, float stress) {
        this.members.put(te, stress);

    }

    public void remove(TileEntityKinetic te) {
        if (!this.members.containsKey(te)) return;
        if (te.isSource()) this.sources.remove(te);
        this.members.remove(te);

        if (this.members.isEmpty()) {
            KNManager.NETWORK_MAP.get(te.getWorld()).remove(this.networkID);
            return;
        }

        this.members.keySet()
                .stream()
                .findFirst()
                .map(member -> member.networkDirty = true);
    }

    public void sync() {
        for (TileEntityKinetic te : this.members.keySet()) updateFromNetwork(te);
    }
    private void updateFromNetwork(TileEntityKinetic te) {
        te.updateFromNetwork(this.capacity, this.stress, this.getSize());
    }

    public void updateCapacity() {
        float newCapacity = this.calculateCapacity();
        if (this.capacity != newCapacity) {
            this.capacity = newCapacity;
            this.sync();
        }
    }
    public void updateStress() {
        float newStress = this.calculateImpact();
        if (this.stress != newStress) {
            this.stress = newStress;
            this.sync();
        }
    }

    public void updateNetwork() {
        float newCapacity = this.calculateCapacity();
        float newStress = this.calculateImpact();
        if (this.capacity != newCapacity || this.stress != newStress) {
            this.capacity = newCapacity;
            this.stress = newStress;
            this.sync();
        }
    }

    public float calculateCapacity() {
        float presentCapacity = 0;
        for (Iterator<TileEntityKinetic> iterator = sources.keySet()
                .iterator(); iterator.hasNext();) {
            TileEntityKinetic te = iterator.next();
            if (te.getWorld()
                    .getTileEntity(te.getPos()) != te) {
                iterator.remove();
                continue;
            }
            presentCapacity += this.getActualCapacityOf(te);
        }
        return presentCapacity + unloadedCapacity;
    }

    public float calculateImpact() {
        float presentStress = 0;
        for (Iterator<TileEntityKinetic> iterator = members.keySet()
                .iterator(); iterator.hasNext();) {
            TileEntityKinetic te = iterator.next();
            if (te.getWorld()
                    .getTileEntity(te.getPos()) != te) {
                iterator.remove();
                continue;
            }
            presentStress += this.getActualStressOf(te);
        }
        return presentStress + unloadedStress;
    }

    public float getActualCapacityOf(TileEntityKinetic te) {
        return this.sources.get(te) * Math.abs(te.getGeneratedSpeed());
    }
    public float getActualStressOf(TileEntityKinetic te) {
        return this.members.get(te) * Math.abs(te.getTheoreticalSpeed());
    }

    public int getSize() {
        return this.unloadedMembers + this.members.size();
    }
}
