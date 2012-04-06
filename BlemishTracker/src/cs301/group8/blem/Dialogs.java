package cs301.group8.blem;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View.OnClickListener;

/** Dialogs class handles the creation of dialogs in our Android app.  For this version, there are two dialogs,
 * the delete item dialog and the add group dialog.
 *
 * @author Group 08 <cs301-group8@ualberta.ca>
 * @version 1.0
 */
public class Dialogs {

	public static final int DIALOG_DELETE_ITEM = 0;
	public static final int DIALOG_ADD_GROUP = 1;
	public static final int DIALOG_CHANGE_PASSWORD = 2;
	public static final int DIALOG_DELETE_ALL = 3;

	/** makeAddGroupDialog is called from onCreateDialog in the MainActivity class when the activity is initiated.
	 * makeAddGroupDialog is a constructor that creates a dialog which enables the user to input the desired group 
	 * name and reminder time in an EditText field. 
	 * 
	 * @param ctx		Context of the activity
	 * @param yes		OnClickListener specifying the method to be executed when "Yes" button is pressed
	 * @param no		OnClickListener specifying the method to be executed when "No" button is pressed
	 *
	 * @return AlertDialog object
	 */
	public static AlertDialog makeDeleteDialog(Context ctx, DialogInterface.OnClickListener yes, DialogInterface.OnClickListener no){
		AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
		builder.setMessage("Are you sure you want to remove item?")
		.setCancelable(false)
		.setPositiveButton("Yes", yes)
		.setNegativeButton("No", no);

		return builder.create();
	}

	/** makeDeleteDialog is called from onCreateDialog in the MainActivity class when the Activity is initiated.
	 * makeDeleteDialog is a constructor that creates a message confirming that the user wanted to delete a group.  
	 * It sets a button for yes and for no.
	 * 
	 * @param ctx		Context of the activity
	 * @param yes		OnClickListener specifying the method to be executed when "Yes" button is pressed
	 * @param no		OnClickListener specifying the method to be executed when "No" button is pressed
	 *
	 * @return AlertDialog object
	 */
	public static Dialog makeAddGroupDialog(Context ctx,  OnClickListener yes, OnClickListener no){
		Dialog dialog = new Dialog(ctx);
		dialog.setContentView(R.layout.add_blemish_group);
		dialog.setTitle("Add Blemish Group");
		dialog.findViewById(R.id.dialog_add_blemish).setOnClickListener(yes);
		dialog.findViewById(R.id.dialog_no_blemish).setOnClickListener(no);

		return dialog;
	}

	/** makeChangePassDialog is called when the user selects the button to set the password. This
	 * dialog will allow the user to save a new password to preferences.
	 * 
	 * @param ctx		Context of the activity
	 * @param yes		OnClickListener specifying the method to be executed when "Yes" button is pressed
	 * @param no		OnClickListener specifying the method to be executed when "No" button is pressed
	 *
	 * @return dialog 	dialog object
	 */
	public static Dialog makeChangePassDialog(Context ctx,  OnClickListener yes, OnClickListener no){
		Dialog dialog = new Dialog(ctx);
		dialog.setContentView(R.layout.changepass);
		dialog.setTitle("Change Password");
		dialog.findViewById(R.id.dialog_ok_password).setOnClickListener(yes);
		dialog.findViewById(R.id.dialog_cancel_password).setOnClickListener(no);

		return dialog;
	}

	/** makeDeleteAll is called from onCreateDialog in the BlemishActivity class when the activity is initiated.
	 * makeDeleteAll is a dialog that confirms whether the user wants to batch delete a subset of pictures 
	 * denoted by checkboxes.
	 * 
	 * @param ctx		Context of the activity
	 * @param yes		OnClickListener specifying the method to be executed when "Yes" button is pressed
	 * @param no		OnClickListener specifying the method to be executed when "No" button is pressed
	 *
	 * @return AlertDialog object
	 */
	public static AlertDialog makeDeleteAll(Context ctx, DialogInterface.OnClickListener yes, DialogInterface.OnClickListener no){
		AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
		builder.setMessage("Are you sure you want to remove selected items?")
		.setCancelable(false)
		.setPositiveButton("Yes", yes)
		.setNegativeButton("No", no);

		return builder.create();
	}
}
