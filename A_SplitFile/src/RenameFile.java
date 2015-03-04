import java.io.File;


public class RenameFile {
	public static void main(String[] args) {
//		removeFile(new File("E:\\1学习\\mysql数据库简明教程"));
		renameFile(new File("D:\\1_SSH\\spring"));
	}

	private static void removeFile(File file) {
		File[] files = file.listFiles(new FilenameFilterByConfig("(1).avi"));
		for (File f : files) {
			if(f.isFile()){
				System.out.println("filename:"+f.getName());
				f.delete();
			}
		}
	}

	private static void renameFile(File file) {
		File [] files = file.listFiles(new FilenameFilterByConfig(".rar"));
		int count=0;
		for (File f : files) {
			if(f.isFile()){
				if(!f.getName().contains("("))
					continue;
				count++;
				StringBuilder sb = new StringBuilder();
//				sb.append(String.valueOf(count));
				String filename = f.getName();
				int start = filename.indexOf('(');
				int end = filename.lastIndexOf(')')+1;
//				System.out.println("start:"+start+"\tend:"+end);
				System.out.println("beforename:"+filename);
				sb.append(filename.substring(0, start));
				sb.append(filename.substring(end));
				filename = sb.toString();
				System.out.println("aftername:"+filename);
				boolean isrename = f.renameTo(new File(f.getParent(),filename));
				System.out.println(isrename);
			}
		}
	}
}
