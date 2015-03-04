import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.SequenceInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;


public class SplitFile {
	private static int SIZE = 1024*1024;				//分解文件单个大小M
	//6267807552
	private static String diskname = null;			//磁盘名称
	private static List<String> list = null;			//存放文件名
	
	public static void main(String[] args) throws IOException {
		menu();
//		showDir();
	}
	
	/*public static void showDirFile(String diskname){
		File file = new File(diskname);
		if(file.isDirectory()){
			File[] files = file.listFiles();
			for (File f : files) {
				if((f.getName().indexOf('.'))!=-1)
					System.out.println("文件夹名称；"+f.getName());
			}
		}
	}*/
	/**
	 * 获得盘符
	 * @param choice
	 * @return
	 */
	public static String getDiskname(String choice){
		String diskname = "";
			switch (choice) {
			case "1":
				diskname = "c:\\";
				break;
			case "2":
				diskname = "d:\\";		
				break;
			case "3":
				diskname = "e:\\";
				break;
			case "4":
				diskname = "f:\\";
				break;
			default:
				String error = "输入错误\n重新输入";
				System.out.println(error);
				diskname = error;
				break;
			}
		return diskname;
	}
	public static void menu() throws IOException{
		Scanner input  = new Scanner(System.in);
		fuck:while(true){
			System.out.println("1分解文件");
			System.out.println("2合并文件");
			System.out.println("0程序结束");
			System.out.println("输入选择");
			String choice = input.next();
			switch(choice){
			case "1":
//				System.out.println("请输入要分解的文件(包含后缀名，如:f:\\xx.mp3)");
				System.out.println("选择盘符\n1C盘:\t2D盘:\t3E盘:\t4F盘");
				
				diskname = getDiskname(input.next());
				if(!diskname.endsWith(":\\")){
					break;
				}
				showDir(diskname);
				
				/*System.out.println("显示全部文件 (Y)");
				String showall = input.next();
				if(showall.equalsIgnoreCase("y")){
					showDirFile(diskname);
				}*/
				System.out.println("请输入要分解的文件(包含后缀名，如:f:\\xx.mp3)");
				StringBuilder bufsplit = new StringBuilder();
				String filename = input.next();
				boolean b = isExsit(filename);
				if(!b){
					System.out.println("文件不存在,或此为文件夹");
					return;
				}
				filename = bufsplit.append(diskname).append(filename).toString();
				
				System.out.println("请输入要分解的单个大小（M:单位）");
				int split_size = input.nextInt();
				System.out.println("请输入分解后的文件存放位置(如:f:\\xxx)");
				String dirname = input.next();
				StringBuilder bufdir = new StringBuilder();
				dirname = bufdir.append(diskname).append(dirname).toString();
				
				long startSplit = System.currentTimeMillis();
				splitFile(new File(filename), new File(dirname),split_size);
				long endSplit = System.currentTimeMillis();
				System.out.println(useTime(endSplit,startSplit));
				break;
			case "2":
				System.out.println("合并文件");
				System.out.println("输入所在文件夹名称");
//				String dirPath = input.next();
				StringBuilder bufpath = new StringBuilder();
				String dirpath = "";
				if(null==diskname)
					dirpath = input.next();
				else
					dirpath = bufpath.append(diskname).append(input.next()).toString();
				
				long startMegre = System.currentTimeMillis();
				mergeFile(new File(dirpath));
				long endMegre = System.currentTimeMillis();
				System.out.println(useTime(endMegre,startMegre));
				break;
			case "0":
				System.out.println("程序结束");
				break fuck;
			default:
				continue;
			}
		}
		
		
		
		
		
		
		
		
		
	}
	

	

	private static String useTime(long endTime, long startTime) {
		long useTime = endTime - startTime;
		
		return "使用"+useTime+"时间\n";
	}

	
	public static void showDir(String diskname){
		list = new ArrayList<String>();
		File file = new File(diskname);
		
		if(file.isDirectory()){
			File[] files = file.listFiles();
			for (File f : files) {
				list.add(f.getName());
				if((f.getName().indexOf('.'))==-1)
					System.out.println("文件夹名称："+f.getName());
				else
					System.out.println("文件名称："+f.getName());
			}
		}
		
	}
	private static boolean isExsit(String filename) {
		Iterator<String> it = list.iterator();
		boolean isExsit = false;
		while(it.hasNext()){
			String name = it.next();
			if(filename.equals(name))
				if((filename.indexOf(".")!=-1))
						isExsit = true;
		}
		return isExsit;
	}
	
	private static void showFilterFilename(File srcDir,String filter){
//		boolean b  = new FilenameFilterByConfig(filter));
		
	}
	private static void mergeFile(File srcDir) throws IOException {
		//健壮性判断
		if(!srcDir.exists()){
			throw new RuntimeException(srcDir.getName()+"不存在");
		}
		//1判断该目录下是否有配置文件
		String[] names = srcDir.list(new FilenameFilterByConfig(".properties"));
		if(names.length!=1){
			throw new RuntimeException("后缀名为properies的文件不错在或者有多个");
		}
		
		File confile = new File(srcDir, names[0]);
		
		//用读取流和配置文件关联
		FileInputStream fis = new FileInputStream(confile);
		
		Properties prop = new Properties();
		
		prop.load(fis);
		
		//读取文件名称和碎片文件个数
		String filename = prop.getProperty("filename");
		int count = Integer.parseInt(prop.getProperty("count"))-1;
		
		File[] partFiles = srcDir.listFiles(new FilenameFilterByConfig(".part"));
		if(partFiles.length!=count){
			throw new RuntimeException("碎片个数错误，不是"+count+"个");
		}
		for(int i = 0; i < count; i++) {
			File file = new File(srcDir,(i+1)+".part");
			if(!file.exists()){
				throw new RuntimeException(file.getName()+"不存在");
			}
		}
		//到这里基本的合并的目录文件判断完毕
		//合并
		merge(srcDir,filename,count);
		
		
		
	}


	private static void merge(File srcDir, String filename, int count) throws IOException {
		List<FileInputStream> list = new ArrayList<FileInputStream>();
		//System.out.println("count:"+count+"\tfilename:"+filename+"\tsrcDir:"+srcDir);
		for(int i=0; i < count; i++){
			list.add(new FileInputStream(new File(srcDir,(i+1)+".part")));
		}
		
		//获取枚举接口对象
		Enumeration<FileInputStream> en = Collections.enumeration(list);
		
		SequenceInputStream sis =new SequenceInputStream(en);
		
		FileOutputStream fos = new FileOutputStream(new File(srcDir, filename));
		byte[] buf = new byte[1024];
		int len = 0;
		
		while((len=sis.read(buf))!=-1){
			fos.write(buf,0,len);
		}
		fos.close();
		sis.close();
		
	}


	/**
	 * 分解文件
	 * @param srcFile
	 * @param destDir
	 * @throws IOException
	 */
	private static void splitFile(File srcFile, File destDir,int split_size) throws IOException {
		//1源
		FileInputStream fis = new FileInputStream(srcFile);
		//2目的
		FileOutputStream fos = null;
		byte[] buf = new byte[SIZE*split_size];
		int count = 1;
		int len = 0;
		//3创建一个Properties集合，用于存储文件信息
		Properties prop = new Properties();
		if(!destDir.exists()){
			destDir.mkdir();
		}
		//对指定的目录进行判断
		while((len=fis.read(buf))!=-1){
			fos = new FileOutputStream(new File(destDir,(count++)+".part"));
			fos.write(buf, 0, len);
			fos.close();
		}
		File confile = new File(destDir,count+".properties");
		
		//在Properties集合存储一些文件信息
		prop.setProperty("filename", srcFile.getName());
		prop.setProperty("count", count+"");
		
		fos = new FileOutputStream(confile);
		prop.store(fos, "");
		
		fos.close();
		fis.close();
		
	}
}
