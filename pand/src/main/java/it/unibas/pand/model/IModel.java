package it.unibas.pand.model;

import android.app.Activity;

import java.util.List;
import java.util.Map;

import it.unibas.pand.observer.Observer;

public interface IModel {
    /**
     * Insert a bean into the model
     *
     * @param id property of the bean
     * @param bean the bean
     * @return Object the proxy of the inserted bean
     */
    Object putBean(String id, Object bean);

    /**
     * Insert a bean into the model without notify the observer
     *
     * @param id property of the bean
     * @param bean the bean
     * @return Object the proxy of the inserted bean
     */
    Object putBeanWithoutNotify(String id, Object bean);

    /**
     * Get the bean from the model
     *
     * @param id property of the bean
     * @return Object the proxy of the inserted bean
     */
    Object getBean(String id);

    /**
     * Pop the bean from the model
     *
     * @param id property of the bean
     * @return Object the proxy of the inserted bean
     */
    Object popBean(String id);

    /**
     * Remove the bean from the model
     *
     * @param id property of the bean
     */
    void removeBean(String id);

    /**
     * Get the value of a bean's field from the model.
     *
     * @param beanNameArray exploded array of a dot notation for retrieve a field from a bean.
     *                     Example: ["person", "address", "route"], equivalent of "person.address.route"
     * @return field value
     */
    Object getBeanValue(String[] beanNameArray);

    /**
     * Set the value of a bean's field
     *
     * @param name dot notation of a field.
     *             Example: "person.address.route"
     * @param value value to set
     */
    void setBeanValue(String name, Object value);

    /**
     * Add an observer for a specified field
     *
     * @param beanDotName dot notation of a bean's field
     * @param observer observer
     */
    void addObserver(String beanDotName, Observer observer);

    /**
     * Remove a specified observer
     *
     * @param beanName dot notation of a bean's field
     * @param observer observer
     */
    void removeObserver(String beanName, Observer observer);

    /**
     * Return the list of all observers
     *
     * @return map with all observers for a specified bean
     */
    Map<String, List<Observer>> getObserverMap();

    /**
     * Return the list of all observers for a specified activity
     *
     * @return map with all observers for a specified activity
     */
    Map<Activity, Map<String, List<Observer>>> getObserverMapActivity();

    /**
     * Remove all the observers for a specified activity
     *
     * @param activity activity
     */
    void cleanObserverForActivity(Activity activity);

    /**
     * Refresh all the observers for a specified activity
     *
     * @param activity activity to refresh
     */
    void refreshActivity(final Activity activity);

    /**
     * Notify all observer that a bean is changed
     *
     * @param beanName dot notation of a bean's field
     */
    void notifyAll(final String beanName);

    /**
     * Notify the observer of the bean changed
     *
     * @param beanName dot notation of a bean's field
     */
    void notifyChange(final String beanName);
}
