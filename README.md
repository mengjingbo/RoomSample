## Android架构组件Room介绍与使用
### 关于Room

Room是Google官方提供个的数据库ORM框架，使用起来非常方便。Room在SQLite上提供了一个抽象层，以便在利用SQLite的全部功能的同时能更加流畅的访问数据库。

Room中三个主要组件：

- Database：该组件用来创建一个database holder。注解定义实体的列表，类的内容定义从数据库中获取数据的对象（DAO）。它也是底层连接的主要入口。这个被注解的类是一个继承RoomDatabase的抽象类。在运行时，可以通过调用Room.databaseBuilder() 或者 Room.inMemoryDatabaseBuilder()来得到它的实例。

- Entity：该组件的一个示例表示数据库的一行数据，对于每个Entity类来说，都会有对应的table被创建。想要这些Entity被创建，就需要卸载上面的Database的注解参属entities列表中，默认Entity中的所有字段都会来创建表，除非该字段上加上@Ignore注解。

- Dao：该组件用来表述具有Data Access Object(DAO)功能的类或者接口，DAO类时Room的重要组件，负责定义查询（添加或者删除等）数据库的方法。使用@Database注解的类必须包含一个0参数的，返回类型为@Dao注解过的类的抽象方法Room会在编译时生成这个类的实现。

### 添加Room库的依赖

首先在Google的Maven存储库(项目最外层的build.gradle文件中添加如下：

```Java
allprojects {
    repositories {
        jcenter()
        google()
    }
}
```

然后再app/build.gradle文件中添加相关依赖

```Java
dependencies {
    compile fileTree(include: ['*.jar'], dir: 'libs')
    // Room依赖
    implementation 'android.arch.persistence.room:runtime:1.0.0'
    annotationProcessor "android.arch.persistence.room:compiler:1.0.0"
    ......
}
```

### 定义Entity

当一个类用 **@Entity** 注解并且被 **@Database** 注解中的 **entities** 属性所引用，Room就会在数据库中为这个被 **@Entity** 注解的类创建一张表。

##### PrimaryKey

每个Entity必须至少有一个主键(Primary Key)，即使只有一个属性，也要使用@PrimaryKey来注释这个属性。如果想让Room为Entity设置自增ID，需要设置@PrimaryKey的autoGenerate属性。

##### ColumnInfo

如果想要自定义表中的字段时，需要在属性上面加上 **@ColumnInfo** 注解，如：@ColumnInfo(name = "ID")，"ID"为自定义字段名。

```Java
@Entity(tableName = "PHONE")
public class PhoneBean implements Parcelable {

    @PrimaryKey(autoGenerate = true) // 设置主键
    @ColumnInfo(name = "ID") // 定义对应的数据库的字段名成
    private int id;

    @ColumnInfo(name = "PHONE")
    private String phone;

    @ColumnInfo(name = "NAME")
    private String name;

    @ColumnInfo(name = "DATE")
    private Date date;

    public PhoneBean(String phone, String name, Date date) {
        this.phone = phone;
        this.name = name;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDate(){
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.phone);
        dest.writeString(this.name);
        dest.writeLong(this.date != null ? this.date.getTime() : -1);
    }

    protected PhoneBean(Parcel in) {
        this.id = in.readInt();
        this.phone = in.readString();
        this.name = in.readString();
        long tmpDate = in.readLong();
        this.date = tmpDate == -1 ? null : new Date(tmpDate);
    }

    public static final Parcelable.Creator<PhoneBean> CREATOR = new Parcelable.Creator<PhoneBean>() {
        @Override
        public PhoneBean createFromParcel(Parcel source) {
            return new PhoneBean(source);
        }

        @Override
        public PhoneBean[] newArray(int size) {
            return new PhoneBean[size];
        }
    };
}
```

### 转换
##### TypeConverter

使用@TypeConverter注解定义转换的方法，如下，将Date类型的数据转换成Long类型来存储。

```Java
public class ConversionFactory {

    @TypeConverter
    public static Long fromDateToLong(Date date) {
        return date == null ? null : date.getTime();
    }

    @TypeConverter
    public static Date fromLongToDate(Long value) {
        return value == null ? null : new Date(value);
    }
}
```

如图，示例中将Date属性值转换为Long类型存储到数据库

![Date类型转换为Long类型存储到数据库](https://github.com/mengjingbo/RoomSample/blob/master/image/1.png?raw=true)

读取到数据库的Date属性值，再将Date属性值转换为字符串显示

![Long类型转换为Date类型展示](https://github.com/mengjingbo/RoomSample/blob/master/image/2.png?raw=true)

### 定义Dao

Dao组件定义一系列的增删改查操作。其中 **Update** 和 **Detele** 操作是根据定义的主键进行。

```Java
@Dao
public interface PhoneDao {

    /**
     * 查询所有
     *
     * @return
     */
    @Query("SELECT * FROM PHONE")
    List<PhoneBean> getPhoneAll();

    /**
     * 根据指定字段查询
     *
     * @return
     */
    @Query("SELECT * FROM PHONE WHERE phone = :phone")
    List<PhoneBean> loadPhoneByIds(String phone);

    /**
     * 项数据库添加数据
     *
     * @param phone
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<PhoneBean> phone);

    /**
     * 修改数据
     *
     * @param phone
     */
    @Update()
    void update(PhoneBean phone);

    /**
     * 删除数据
     *
     * @param phoneBean
     */
    @Delete()
    void delete(PhoneBean phoneBean);
}
```

### 创建数据库

```Java
@Database(entities = {PhoneBean.class}, version = 1, exportSchema = false)
@TypeConverters({ConversionFactory.class})
public abstract class PhoneDatabase extends RoomDatabase {

    public static PhoneDatabase getDefault(Context context) {
        return buildDatabase(context);
    }

    private static PhoneDatabase buildDatabase(Context context) {
        return Room.databaseBuilder(context.getApplicationContext(), PhoneDatabase.class, "PHONE.db")
                .allowMainThreadQueries()
                .build();
    }

    public abstract PhoneDao getPhoneDao();
}
```

### 使用

##### 增加

```Java
private void insertPhone(String mName, String mPhone) {
    List<PhoneBean> mPhones = new ArrayList<>();
    mPhones.add(new PhoneBean(mPhone, mName));
    PhoneDatabase.getDefault(getApplicationContext()).getPhoneDao().insertAll(mPhones);
}
```

##### 查询

```Java
private void queryPhone() {
    List<PhoneBean> mPhoneLists = PhoneDatabase.getDefault(getApplicationContext()).getPhoneDao().getPhoneAll();
    // 其他代码......
}
```

##### 修改

```Java
private void updatePhone(String name, String phone) {
    PhoneDatabase.getDefault(getActivity().getApplicationContext()).getPhoneDao().update(new PhoneBean(phone, name));
    // ......
}
```

##### 删除

```Java
private void deletePhone() {
    PhoneDatabase.getDefault(getActivity().getApplicationContext()).getPhoneDao().delete(mPhoneBean);
    // ......
}
```
