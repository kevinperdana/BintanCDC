package cdc.bintan.com.bintancdc;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class DaftarFragmentSerialisasi implements Serializable {
    @SerializedName("username")
    public String username;

    @SerializedName("name")
    public String name;

    @SerializedName("nomor_hp")
    public String nomor_hp;

    @SerializedName("alamat")
    public String alamat;

    @SerializedName("PmKecamatanID")
    public String PmKecamatanID;

    @SerializedName("Nm_Kecamatan")
    public String Nm_Kecamatan;

    @SerializedName("PmDesaID")
    public String PmDesaID;

    @SerializedName("Nm_Desa")
    public String Nm_Desa;

    @SerializedName("foto")
    public String foto;

    @SerializedName("status_pasien")
    public String status_pasien;

    @SerializedName("nama_status")
    public String nama_status;

    @SerializedName("payload")
    public String payload;
}
