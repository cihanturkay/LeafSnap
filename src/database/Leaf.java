package database;

import android.graphics.Bitmap;

public class Leaf {
	
	private int id;
	private int photoId;
	private String name;
	private String turkishName;
	private String detail;
	private String phylum;	//bölüm
	private String clazz; 	//sýnýf
	private String order;	//takým
	private String family;	//familya
	private String genus;	//cins
	private String imagePath;
	private Bitmap image;
	private String longitude;
	private String latitude;
	private String time;
	private boolean isLocal = false;
	private boolean isUploaded = false;
	
	public Leaf(){
	}
	
	public Leaf(int id, String name, String turkishName, String detail, String phylum, String clazz, String order,
			String family, String genus, Bitmap image, String longitude, String latitude) {
		super();
		this.id = id;
		this.name = name;
		this.turkishName = turkishName;
		this.detail = detail;
		this.phylum = phylum;
		this.clazz = clazz;
		this.order = order;
		this.family = family;
		this.genus = genus;
		this.image = image;
		this.longitude = longitude;
		this.latitude = latitude;
	}
	

	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTurkishName() {
		return turkishName;
	}

	public void setTurkishName(String turkishName) {
		this.turkishName = turkishName;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	public String getPhylum() {
		return phylum;
	}

	public void setPhylum(String phylum) {
		this.phylum = phylum;
	}

	public String getClazz() {
		return clazz;
	}

	public void setClazz(String clazz) {
		this.clazz = clazz;
	}

	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}

	public String getFamily() {
		return family;
	}

	public void setFamily(String family) {
		this.family = family;
	}

	public String getGenus() {
		return genus;
	}

	public void setGenus(String genus) {
		this.genus = genus;
	}

	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String image) {
		this.imagePath = image;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public boolean isLocal() {
		return isLocal;
	}

	public void setLocal(boolean isLocal) {
		this.isLocal = isLocal;
	}

	public boolean isUploaded() {
		return isUploaded;
	}

	public void setUploaded(boolean isUploaded) {
		this.isUploaded = isUploaded;
	}

	public Bitmap getImage() {
		return image;
	}

	public void setImage(Bitmap image) {
		this.image = image;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public int getPhotoId() {
		return photoId;
	}

	public void setPhotoId(int photoId) {
		this.photoId = photoId;
	}

	
	
}
