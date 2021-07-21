
    /**
     * 新增预约
     * @param order
     * @return
     */
    int save(Order order);

    /**
     * 判断时间段a-b是否与已有的预约时间重复
     * @param order
     * @return
     */
    List<Order> judgeTimeCorrect(Order order);

}
