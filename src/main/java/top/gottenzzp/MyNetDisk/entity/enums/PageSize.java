package top.gottenzzp.MyNetDisk.entity.enums;


import lombok.Getter;

/**
 * @author gottenzzp
 */

@Getter
public enum PageSize {
	// 15 20 30 40 50
	SIZE15(15), SIZE20(20), SIZE30(30), SIZE40(40), SIZE50(50);
	final int size;

	private PageSize(int size) {
		this.size = size;
	}

}
