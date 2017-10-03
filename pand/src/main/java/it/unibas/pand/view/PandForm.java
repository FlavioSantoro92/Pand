package it.unibas.pand.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;

import it.unibas.pand.Pand;
import it.unibas.pand.R;
import it.unibas.pand.Utility;
import it.unibas.pand.control.IValidFormAction;
import it.unibas.pand.validator.ErrorMessage;
import it.unibas.pand.validator.FormValidator;
import it.unibas.pand.exception.PandException;
import it.unibas.pand.linker.ILinker;

public class PandForm extends FrameLayout {
    public static final String TAG = PandForm.class.getName();
    private int buttonCommitId;
    private int buttonRollbackId;
    private boolean showErrorDialog = true;
    private List<ILinker> linkerList;
    private List<ErrorMessage> errorMessageList;
    private List<FormValidator> validatorList = new ArrayList<>();
    private IValidFormAction validFormAction;
    private List<BeanToCreate> beanToCreateList = new ArrayList<>();

    private class BeanToCreate{
        private String name;
        private Class bClass;

        BeanToCreate(String name, Class bClass) {
            this.name = name;
            this.bClass = bClass;
        }

        String getName() {
            return name;
        }

        Class getbClass() {
            return bClass;
        }
    }

    public PandForm(@NonNull Context context) {
        super(context);
    }

    public PandForm(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        getAttributes(context, attrs);
    }

    public PandForm(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        getAttributes(context, attrs);
    }

    public void addValidator(FormValidator c) {
        validatorList.add(c);
    }

    public IValidFormAction getValidFormAction() {
        return validFormAction;
    }

    public void setValidFormAction(IValidFormAction validFormAction) {
        this.validFormAction = validFormAction;
    }

    public void setAutoCreate(String beanToCreateName, Class beanClass) {
        beanToCreateList.add(new BeanToCreate(beanToCreateName, beanClass));
    }

    public List<ErrorMessage> getErrorMessageList() {
        return errorMessageList;
    }

    public void setError(String errorMessage){
        AlertDialog.Builder builder = new AlertDialog.Builder(Pand.getInstance().getCurrentActivity());
        builder.setTitle(R.string.error);
        builder.setMessage(errorMessage);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void getAttributes(Context context, AttributeSet attrs){
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PandForm, 0, 0);
        buttonCommitId = a.getResourceId(R.styleable.PandForm_buttonCommit, 0);
        buttonRollbackId = a.getResourceId(R.styleable.PandForm_buttonRollback, 0);
        showErrorDialog = a.getBoolean(R.styleable.PandForm_shotErrorDialog, true);
        String formActionText = a.getString(R.styleable.PandForm_validAction);
        a.recycle();
        Log.d(TAG, "buttonCommitId: " + buttonCommitId);
        Log.d(TAG, "buttonRollbackId: " + buttonRollbackId);
        setValidActionFromName(formActionText);

        getRootView().getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Log.d(TAG, "full inflated");
                /*
                 * Set the button commit click listener
                 */
                View buttonCommit = getRootView().findViewById(buttonCommitId);
                if(buttonCommit != null) {
                    buttonCommit.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Log.d(TAG, "Commit.");
                            commit();
                        }
                    });
                } else {
                    Log.e(TAG, "no commit button found");
                }
                /*
                 * Set the rollback button click listener
                 */
                View buttonRollback = getRootView().findViewById(buttonRollbackId);
                if(buttonRollback != null) {
                    buttonRollback.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Log.d(TAG, "Rollback.");
                            rollback();
                        }
                    });
                } else {
                    Log.e(TAG, "no rollback button found");
                }

                getRootView().getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }

    private void setValidActionFromName(String formActionText){
        Log.d(TAG, "formAction: " + formActionText);
        if(formActionText != null && !formActionText.equals("")){
            try {
                IValidFormAction validFormAction = (IValidFormAction) Utility.createObject(formActionText);
                setValidFormAction(validFormAction);
            } catch (PandException e) {
                Log.e(TAG, "Unable to set the valid action " + formActionText);
            }
        }
    }

    private void navigateTree(View v){
        if(v instanceof ILinker){
            linkerList.add(((ILinker) v));
        } else if(v instanceof ViewGroup){
            for(int i = 0; i < ((ViewGroup) v).getChildCount(); i++){
                View child = ((ViewGroup) v).getChildAt(i);
                navigateTree(child);
            }
        }
    }

    private void commit(){
        linkerList = new ArrayList<>();
        errorMessageList = new ArrayList<>();
        navigateTree(this);
        for(ILinker linker : linkerList){
            List<ErrorMessage> linkerErrorMessage = linker.checkAllValidator();
            if(linkerErrorMessage != null) {
                errorMessageList.addAll(linkerErrorMessage);
            }
        }

        for(FormValidator convalidator : validatorList){
            convalidator.validate(this);
            List<ErrorMessage> formErrorMessageList = convalidator.getErrorList();
            if(formErrorMessageList != null) {
                errorMessageList.addAll(formErrorMessageList);
            }
        }

        if(errorMessageList.size() == 0){
            for(BeanToCreate beanToCreate : beanToCreateList){
                if(beanToCreate.getName() != null && beanToCreate.getbClass() != null){
                    Object existBean = Pand.getInstance().getModel().getBean(beanToCreate.getName());
                    if(existBean == null) {
                        Log.d(TAG, "The bean " + beanToCreate.getName() + " not exist in the model");
                        try {
                            Object bean = Utility.createObject(beanToCreate.getbClass().getName());
                            Pand.getInstance().getModel().putBeanWithoutNotify(beanToCreate.getName(), bean);
                        } catch (PandException e) {
                            Log.e(TAG, "PandException: " + e.getLocalizedMessage());
                        }
                    } else {
                        Log.d(TAG, "The bean " + beanToCreate.getName() + " already exist in the model!");
                    }
                }
            }

            for(ILinker linker : linkerList){
                linker.link();
            }

            for(BeanToCreate beanToCreate : beanToCreateList){
                Pand.getInstance().getModel().notifyChange(beanToCreate.getName());
            }

            if(validFormAction != null){
                validFormAction.onValidate();
            }
        } else {
            if(showErrorDialog) {
                StringBuilder builder = new StringBuilder();
                boolean isFirst = true;
                for (ErrorMessage errorMessage : errorMessageList) {
                    if (!errorMessage.isCustomErrorView()) {
                        if (isFirst) {
                            isFirst = false;
                        } else {
                            builder.append("\n");
                        }
                        builder.append(errorMessage.getMessage());
                    }
                }
                if (builder.length() != 0) {
                    setError(builder.toString());
                }
            }
        }
    }

    private void rollback(){
        linkerList = new ArrayList<>();
        errorMessageList = new ArrayList<>();
        navigateTree(this);
        for(ILinker linker : linkerList){
            linker.clear();
        }
    }
}
