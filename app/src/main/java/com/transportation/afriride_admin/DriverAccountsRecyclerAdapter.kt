package com.transportation.afriride_admin

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

class DriverAccountsRecyclerAdapter(
    private val context: Context,
    private val driverAccountsList: List<DriverAccount>
    ):RecyclerView.Adapter<DriverAccountsRecyclerAdapter.MyViewHolder>() {

    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val driverAccountCard: CardView = itemView.findViewById(R.id.driverAccountCard)
        val driverProfilePic: ImageView = itemView.findViewById(R.id.driverProfilePic)
        val driverLicensePlate: TextView = itemView.findViewById(R.id.driverLicensePlate)
        val driverName: TextView = itemView.findViewById(R.id.driverName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.driver_accounts_recycler_list_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val driverAccount = driverAccountsList[position]
        holder.driverAccountCard.setOnClickListener{
            openDriverAccountPage()
        }

        holder.driverProfilePic.setImageResource(driverAccount.profilePic)
        holder.driverLicensePlate.text = driverAccount.licensePlate
        holder.driverName.text = driverAccount.driverName
    }

    private fun openDriverAccountPage() {
        //TODO: "Not yet implemented"
    }

    override fun getItemCount(): Int {
        return driverAccountsList.size
    }
}