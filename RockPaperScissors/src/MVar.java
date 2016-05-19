class MVar<E>
{
    private E state;
    private boolean isSet = false;

    public synchronized void putMVar(E s) {
        while (isSet) {
            try {
                wait();
            } catch (InterruptedException e) {
                // handle exception
            }
        }
        isSet = true;
        state = s;
        notifyAll();
    }

    public synchronized E takeMVar() {
        while (!isSet) {
            try {
                wait();
            } catch (InterruptedException e) {
                // handle exception
            }
        }
        isSet = false;
        notifyAll();
        return state;
    }
}
