def createDate(year, month, day) {
        c = Calendar.getInstance()
        c.clear()
        c.set(year, month - 1, day, 0, 0, 0);
        return c.getTime()
}
